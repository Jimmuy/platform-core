package com.qcec.dataservice.request;

import com.qcec.app.QCApplication;
import com.qcec.dataservice.base.CacheStrategy;
import com.qcec.utils.PreferenceUtils;
import com.qcec.utils.SystemUtils;
import com.qcec.dataservice.request.RequestBody.FormBody;
import com.qcec.dataservice.request.RequestBody.JsonBody;

import java.util.Iterator;
import java.util.Map;

public class BasicApiRequest extends BasicHttpRequest implements ApiRequest {

    private int cacheStrategy = CacheStrategy.NONE;

    public BasicApiRequest(String url) {
        this(url, HttpMethod.POST, CacheStrategy.NONE, DEFAULT_TIMEOUT);
    }

    public BasicApiRequest(String url, String method) {
        this(url, method, CacheStrategy.NONE, DEFAULT_TIMEOUT);
    }

    public BasicApiRequest(String url, String method, int cacheStrategy) {
        this(url, method, cacheStrategy, DEFAULT_TIMEOUT);
    }

    public BasicApiRequest(String url, String method, int cacheStrategy, int timeout) {
        super(url, method, timeout);
        this.cacheStrategy = cacheStrategy;
    }

    public int getCacheStrategy() {
        return cacheStrategy;
    }

    public String getCacheKey() {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(getUrl()+"?");

        RequestBody body = getBody();
        if(body instanceof FormBody) {
            FormBody formBody = (FormBody)body;

            Iterator iterator = formBody.getParams().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry)iterator.next();
                urlBuilder.append(entry.getKey()).append("=");
                urlBuilder.append(entry.getValue()).append("&");
            }

        } else if (body instanceof JsonBody) {
            JsonBody jsonBody = (JsonBody) body;

            urlBuilder.append("body=");
            urlBuilder.append(jsonBody.getJson()).append("&");
        }

        String userId = PreferenceUtils.getPrefString(QCApplication.getInstance(), "id", "");
        urlBuilder.append(userId).append(SystemUtils.getPackageName(QCApplication.getInstance()));

        return urlBuilder.toString();
    }

    @Override
    public void formatRequestParams() {

    }

}
