package com.qcec.dataservice.base;

/**
 * Define cache strategy.
 * Created by chen on 15/3/18.
 */
public interface CacheStrategy {

    /**
     * None cache strategy
     */
    int NONE = 0;

    /**
     * Default cache strategy, 5 min
     */
    int NORMAL = 1;

    /**
     * Cache for 1 hour
     */
    int HOURLY = 2;

    /**
     * Cache for 1 day
     */
    int DAILY = 3;

    /**
     * Persist cache
     */
    int PERSIST = 4;

    /**
     * Cache precedence strategy
     * If cache exits, use cache first and send request at same time.
     * After request finished, data and cache will be updated.
     * To use this strategy, you should not set request == null, otherwise only cache will be updated.
     */
    int CACHE_PRECEDENCE = 5;
}
