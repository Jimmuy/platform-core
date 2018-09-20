package com.qcec.dataservice.request;

public interface ApiRequest extends HttpRequest {

    /**
     * Get cache strategy
     *
     * @return
     */
    int getCacheStrategy();

    /**
     * Get Cache Key
     * @return
     */
    String getCacheKey();

    /**
     * Format request params
     */
    void formatRequestParams();

}
