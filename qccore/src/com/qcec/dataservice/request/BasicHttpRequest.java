package com.qcec.dataservice.request;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;

public class BasicHttpRequest implements HttpRequest {

    protected static final int DEFAULT_TIMEOUT = 15000;

    private String url;
    private String method;
    private int timeout;
    private Map<String, String> headers = new HashMap<String, String>();

    private RequestBody body;

    public BasicHttpRequest(String url) {
        this(url, HttpMethod.GET, DEFAULT_TIMEOUT);
    }

    public BasicHttpRequest(String url, String method) {
        this(url, method, DEFAULT_TIMEOUT);
    }

    public BasicHttpRequest(String url, String method, int timeout) {
        this.url = url;
        this.method = method;
        this.timeout = timeout;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getMethod() {
        return method.toString();
    }

    @Override
    public int getTimeout() {
        return timeout;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        setHeaders(headers, false);
    }

    public void setHeaders(Map<String, String> headers, boolean clear) {
        if (clear) {
            this.headers = headers;
        } else {
            this.headers.putAll(headers);
        }
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setBody(RequestBody body) {
        if(!HttpMethod.permitsRequestBody(method)) return;
        this.body = body;
    }

    public RequestBody getBody() {
        return body;
    }

}
