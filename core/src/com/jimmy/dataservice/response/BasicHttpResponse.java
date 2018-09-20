package com.jimmy.dataservice.response;

import java.util.HashMap;
import java.util.Map;

public class BasicHttpResponse implements HttpResponse {

    private byte[] result;
    private byte[] error;
    private Map<String, String> headers = new HashMap<String, String>();
    private int statusCode;

    public BasicHttpResponse(int statusCode, Map<String, String> headers, byte[] result,
            byte[] error) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.result = result;
        this.error = error;
    }
    
    @Override
    public byte[] getResult() {
        return result;
    }

    @Override
    public byte[] getError() {
        return error;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }
    
}
