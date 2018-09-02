package com.qcec.dataservice.request;

/**
 * Created by sunyun on 16/4/22.
 */
public final class HttpMethod {

    private HttpMethod() {}

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";

    public static boolean requiresRequestBody(String method) {
        return method.equals("POST")
                || method.equals("PUT");
    }

    public static boolean permitsRequestBody(String method) {
        return requiresRequestBody(method)
                || method.equals("OPTIONS")
                || method.equals("DELETE");
    }
}
