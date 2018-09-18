package com.qcec.dataservice.service;

import android.content.Context;

import com.qcec.dataservice.request.HttpRequest;
import com.qcec.dataservice.response.BasicHttpResponse;
import com.qcec.dataservice.response.HttpResponse;
import com.qcec.log.QCLog;
import com.qcec.utils.NetworkUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class HttpEngine {

    private Context context;
    private HttpRequest request;


    public HttpEngine(Context context, HttpRequest request) {
        this.context = context;
        this.request = request;
    }

    public HttpResponse getResponse(IProgress progress) {
        if (NetworkUtils.getNetworkClass(context) == NetworkUtils.NETWORK_TYPE_UNAVAILABLE) {
            return new BasicHttpResponse(1025, request.getHeaders(), null, "网络已关闭".getBytes());
        }

        long startTime = System.currentTimeMillis();

        HttpResponse response;
        try {
            response = sendSyncRequest(progress);
        } catch (SocketTimeoutException e) {
            response = new BasicHttpResponse(1001, null, null, "网络超时".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            response = new BasicHttpResponse(0, null, null, null);
        }

        long elapse = System.currentTimeMillis() - startTime;
        StringBuilder sb = new StringBuilder();
        if (response.getResult() != null) {
            sb.append("finish (");
        } else {
            sb.append("fail (");
        }
        sb.append(request.getMethod() + ",");
        sb.append(response.getStatusCode()).append(',');
        sb.append(elapse).append("ms");
        sb.append(") ").append(request.getUrl());
        QCLog.i(sb.toString());

        return response;

    }

    private HttpResponse sendSyncRequest(IProgress progress) throws IOException {
        String url = request.getUrl();
        String method = request.getMethod();
        RequestBody body = request.getBody();
        Headers headers = Headers.of(request.getHeaders());
        Request request = new Request.Builder().url(url).method(method, body).headers(headers).build();

        OkHttpClient okHttpClient = new OkHttpClient();
        Call call = okHttpClient.newCall(request);
        Response execute = call.execute();

        if (execute.code() == 0) {
            throw new IOException("Could not retrieve response code from OkHttp.");
        }

        HashMap<String, String> responseHeaders = new HashMap<String, String>();
        for (Map.Entry<String, List<String>> header : headers.toMultimap().entrySet()) {
            if (header.getKey() != null) {
                responseHeaders.put(header.getKey(), header.getValue().get(0));
            }
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            InputStream input;
            if (execute.isSuccessful()) {
                input = execute.body().byteStream();
                int totalContent = Integer.valueOf(execute.header("content-length", "-1"));

                if (input == null) {
                    throw new IOException();
                }

                if ("gzip".equals(execute.header("content-encoding"))) {
                    input = new GZIPInputStream(input);
                }

                int count;
                if (totalContent > 0) {
                    while ((count = input.read(buffer)) != -1) {
                        output.write(buffer, 0, count);
                        if (progress != null) {
                            progress.reportProgress(output.size(), totalContent);
                        }
                    }
                } else {
                    while ((count = input.read(buffer)) != -1) {
                        output.write(buffer, 0, count);
                    }
                }
                while ((count = input.read(buffer)) != -1) {
                    output.write(buffer, 0, count);
                }
                return new BasicHttpResponse(execute.code(), responseHeaders, output.toByteArray(), null);
            } else {
                input = execute.body().byteStream();
                int count;
                while ((count = input.read(buffer)) != -1) {
                    output.write(buffer, 0, count);
                }
                return new BasicHttpResponse(execute.code(), responseHeaders, null, output.toByteArray());
            }
        } finally {
            if (output != null) {
                output.close();
            }
        }

    }

    public interface IProgress {
        void reportProgress(int count, int total);
    }

}
