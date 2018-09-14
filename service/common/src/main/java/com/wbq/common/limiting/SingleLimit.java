package com.wbq.common.limiting;

import com.google.common.util.concurrent.RateLimiter;

/**
 *  * service limiting base on RateLimiter
 *      <p>only useful for single jvm</p>
 *  * @author biqin.wu
 *  * @since 14 九月 2018
 *  
 */
public class SingleLimit {
    /**
     * permit per second
     *
     * @param permitsPerSecond the number of permit generate per second
     * @return rateLimiter
     */
    public static RateLimiter limit(double permitsPerSecond) {
        return RateLimiter.create(permitsPerSecond);
    }
}
