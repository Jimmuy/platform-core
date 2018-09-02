package com.qcec.dataservice.service;

import android.content.Context;
import android.os.AsyncTask;

import com.qcec.cache.RequestCache;
import com.qcec.datamodel.ResultModel;
import com.qcec.dataservice.base.CacheStrategy;
import com.qcec.dataservice.base.DataService;
import com.qcec.dataservice.base.RequestHandler;
import com.qcec.utils.NetworkQuality;
import com.qcec.dataservice.request.ApiRequest;
import com.qcec.dataservice.response.ApiResponse;
import com.qcec.dataservice.response.BasicApiResponse;
import com.qcec.dataservice.response.HttpResponse;
import com.qcec.log.QCLog;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

public class ApiService implements DataService<ApiRequest, ApiResponse> {

    private Context context;
    private Executor executor;

    private RequestCache requestCache;
    private ApiInterceptor interceptor;

    private final ConcurrentHashMap<ApiRequest, ApiTask> runningTasks = new ConcurrentHashMap<>();

    public ApiService(Context context) {
        this(context, ExecutorUtil.DEFAULT_EXECUTOR);
    }
    
    public ApiService(Context context, Executor executor) {
        this.context = context;
        this.executor = executor;
        this.requestCache = RequestCache.getInstance(context);
    }

    public void setInterceptor(ApiInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void exec(ApiRequest req, RequestHandler<ApiRequest, ApiResponse> handler) {
        req.formatRequestParams();

        ApiTask currentTask = new ApiTask(req, handler);
        ApiTask prevTask = runningTasks.putIfAbsent(req, currentTask);
        if (prevTask == null) {
            currentTask.executeOnExecutor(executor);
        } else {
            QCLog.e("api cannot exec duplicate request (same instance)");
        }
    }

    @Override
    public void abort(ApiRequest req, RequestHandler<ApiRequest, ApiResponse> handler,
                      boolean mayInterruptIfRunning) {
        ApiTask runningTask = runningTasks.get(req);
        if (runningTask != null && runningTask.handler == handler) {
            runningTasks.remove(req, runningTask);
            runningTask.cancel(mayInterruptIfRunning);
        }
    }

    public void clearCache() {
        requestCache.clear();
    }


    protected class ApiTask extends AsyncTask<Void, Integer, ApiResponse> implements HttpEngine.IProgress{

        protected final ApiRequest req;
        protected final RequestHandler<ApiRequest, ApiResponse> handler;

        protected HttpEngine httpEngine;

        public ApiTask(ApiRequest req, RequestHandler<ApiRequest, ApiResponse> handler) {
            super();

            this.req = req;
            this.handler = handler;
            httpEngine = new HttpEngine(context, req);
        }

        @Override
        protected void onPreExecute() {
            if(interceptor != null) {
                if(interceptor.beforeRequest(req, handler)) {
                    return;
                }
            }
            if (handler != null) {
                handler.onRequestStart(req);
            }
        }

        @Override
        protected ApiResponse doInBackground(Void... params) {

            if (req.getCacheStrategy() != CacheStrategy.NONE) {
                final ApiResponse resp = requestCache.get(req);
                if (resp != null) {
                    QCLog.i("finish (CACHE STRATEGY:" + req.getCacheStrategy() + ") " + req.getUrl());
                    if (req.getCacheStrategy() != CacheStrategy.CACHE_PRECEDENCE) {
                        return resp;
                    }

                    if(!isCancelled()) {
                        ExecutorUtil.MAIN_THREAD_EXECUTOR.execute(new Runnable() {
                            @Override
                            public void run() {
                                handleResponse(resp);
                            }
                        });
                    }
                }
            }

            Random random = new Random();
            int number = random.nextInt(100);
            if (number < NetworkQuality.current().percentage) {
                QCLog.e("网络质量随机数：" + number + "  百分比" + NetworkQuality.current().percentage);
                return new BasicApiResponse(1024, req.getHeaders(), null, "网络异常".getBytes());
            }

            HttpResponse httpResp = httpEngine.getResponse(this);
            ApiResponse resp = new BasicApiResponse(httpResp.getStatusCode(), httpResp.getHeaders(),
                    httpResp.getResult(), httpResp.getError());
            if (req.getCacheStrategy() != CacheStrategy.NONE
                    && resp.getStatusCode() == 200 ) {
                ResultModel resultModel = resp.getResultModel();
                if(resultModel.status == 0 || resultModel.code == 0) {
                    requestCache.put(req, resp);
                }
            }
            return resp;
        }

        @Override
        protected void onPostExecute(ApiResponse response) {
            runningTasks.remove(req, this);

            handleResponse(response);
        }

        private void handleResponse(ApiResponse response) {
            if (response.getStatusCode() >= 200 && response.getStatusCode() <= 299) {
                if(interceptor != null) {
                    if(interceptor.afterRequest(req, response, handler)) {
                        return;
                    }
                }

                if(handler != null) {
                    handler.onRequestFinish(req, response);
                }
            } else {
                if (handler != null) {
                    handler.onRequestFailed(req, response);
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (handler != null) {
                handler.onRequestProgress(req, values[0], values[1]);
            }
        }

        @Override
        public void reportProgress(int count, int total) {
            publishProgress(count, total);
        }


    }

    public interface ApiInterceptor {

        boolean beforeRequest(ApiRequest req, RequestHandler<ApiRequest, ApiResponse> handler);

        boolean afterRequest(ApiRequest req, ApiResponse resp, RequestHandler<ApiRequest, ApiResponse> handler);
    }
}
