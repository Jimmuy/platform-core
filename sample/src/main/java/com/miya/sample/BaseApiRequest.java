package com.miya.sample;


import android.support.v4.app.FragmentTabHost;

import com.qcec.app.QCApplication;
import com.qcec.dataservice.request.BasicApiRequest;
import com.qcec.dataservice.request.JsonBody;
import com.qcec.utils.SystemUtils;

import java.util.Map;

/**
 * Created by sunyun on 16/5/19.
 */
public class BaseApiRequest extends BasicApiRequest {
    Map<String, Object> params;
    Object data;

    public BaseApiRequest(String url) {
        super(getAbsoluteUrl(url));
    }

    public BaseApiRequest(String url, String method) {
        super(getAbsoluteUrl(url), method);
    }

    public BaseApiRequest(String url, String method, int cacheStrategy) {
        super(getAbsoluteUrl(url), method, cacheStrategy);
    }

    public BaseApiRequest(String url, String method, int cacheStrategy, int timeout) {
        super(getAbsoluteUrl(url), method, cacheStrategy, timeout);
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Map<String, Object> getParams() {
        return this.params;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public static String getAbsoluteUrl(String url) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        if (url.startsWith("//")) {
            return "http:" + url;
        }
        return "DomainConfig.apiDomain.url" + url;
    }

    @Override
    public void formatRequestParams() {
        StringBuilder agentBuilder = new StringBuilder();
        agentBuilder.append("(Android;").append(android.os.Build.VERSION.RELEASE).append(";").append(android.os.Build.MODEL).append(")");
        addHeader("Agent", agentBuilder.toString());
        addHeader("VersionCode", SystemUtils.getVersionName(QCApplication.getInstance()));
        addHeader("Accept", "application/json");

        if (params != null) {
            setBody(new JsonBody(params));
        }
    }
}
