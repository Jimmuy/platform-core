package com.jimmy.dataservice.request;

import com.jimmy.app.CoreApplication;
import com.jimmy.dataservice.base.CacheStrategy;
import com.jimmy.utils.PreferenceUtils;
import com.jimmy.utils.SystemUtils;


import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class BasicApiRequest extends BasicHttpRequest implements ApiRequest {
    private static final MediaType CONTENT_FORM_TYPE = MediaType.get("application/x-www-form-urlencoded");
    private static final MediaType CONTENT_JSON_TYPE = MediaType.get("application/json");
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
        urlBuilder.append(getUrl() + "?");

        RequestBody body = getBody();
        if (CONTENT_FORM_TYPE.equals(body.contentType())) {
            FormBody formBody = (FormBody) body;
            for (int i = 0; i < formBody.size(); i++) {
                urlBuilder.append(formBody.name(i)).append("=");
                urlBuilder.append(formBody.value(i)).append("&");
            }
        } else if (CONTENT_JSON_TYPE.equals(body.contentType())) {
            JsonBody jsonBody = (JsonBody) body;
            urlBuilder.append("body=");
            urlBuilder.append(jsonBody.getParams()).append("&");
        }

        String userId = PreferenceUtils.getPrefString(CoreApplication.getInstance(), "id", "");
        urlBuilder.append(userId).append(SystemUtils.getPackageName(CoreApplication.getInstance()));

        return urlBuilder.toString();
    }

    @Override
    public void formatRequestParams() {

    }

}
