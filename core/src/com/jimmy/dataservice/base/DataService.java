package com.jimmy.dataservice.base;

/**
 * DataService interface used to get data from http or cache.
 */

public interface DataService<T extends Request, R extends Response> {

    /**
     * Asynchronous request executer.
     * The result will be handler by RequestHandler.
     *
     * @param req
     * @param handler
     */
    void exec(T req, RequestHandler<T, R> handler);

    /**
     * Abort a request if necessary.
     *
     * @param req
     * @param handler
     * @param mayInterruptIfRunning
     */
    void abort(T req, RequestHandler<T, R> handler, boolean mayInterruptIfRunning);
}
