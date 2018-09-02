package com.qcec.dataservice.base;

/**
 * Callback used to deal with asynchronous request.
 *
 * 
 */
public interface RequestHandler<T extends Request, R extends Response> {
	
	
	/**
	 * Called when request start.
	 */
	void onRequestStart(T req);

	/**
	 * Called when request is running.
	 */
	void onRequestProgress(T req, int count, int total);

	/**
	 * Called if request finished.
	 */
	void onRequestFinish(T req, R resp);

	/**
	 * Called if request is failed.
	 */
	void onRequestFailed(T req, R resp);
}
