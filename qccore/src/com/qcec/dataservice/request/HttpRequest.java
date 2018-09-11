package com.qcec.dataservice.request;

import com.qcec.dataservice.base.Request;

import java.util.Map;

import okhttp3.RequestBody;

public interface HttpRequest extends Request{
	
	/**
	 * Get http request method.
	 * @return
	 */
	String getMethod();
	
	/**
	 * Get http request headers.
	 * @return
	 */
	Map<String, String> getHeaders();
	
	/**
	 * Get request timeout in millisecond.
	 * @return
	 */
	int getTimeout();

	/**
	 * Get Request Body
	 * @return
	 */
	RequestBody getBody();
}
