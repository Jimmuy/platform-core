package com.qcec.dataservice.response;

import java.util.HashMap;
import java.util.Map;

import com.qcec.dataservice.base.Response;

public interface HttpResponse extends Response{
    
	/**
	 * HTTP status code
	 * @return
	 */
    int getStatusCode();

    /**
     * Get HTTP response headers
     * @return
     */
    Map<String, String> getHeaders();
    
}
