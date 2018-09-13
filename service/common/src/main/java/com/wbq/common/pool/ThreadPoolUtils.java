package com.wbq.common.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *  *
 *  * @author biqin.wu
 *  * @since 13 九月 2018
 *  
 */
public class ThreadPoolUtils {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolUtils.class);

    /**
     * close thread pool graceful
     *
     * @param pool thread pool
     * @throws InterruptedException exception
     */
    public static void close(ThreadPoolExecutor pool) throws InterruptedException {
        long start = System.currentTimeMillis();
        pool.shutdown();
        while (!pool.awaitTermination(1, TimeUnit.SECONDS)) {
            logger.info("pool has thread still running ");
        }
        logger.info("success to close pool pool={} using time={}", pool, System.currentTimeMillis() - start);
    }
}
