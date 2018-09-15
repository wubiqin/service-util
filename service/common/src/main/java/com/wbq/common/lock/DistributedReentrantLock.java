package com.wbq.common.lock;

import java.util.concurrent.TimeUnit;

/**
 * r
 *  * distributed lock that can reentrant acquire
 *  * @author biqin.wu
 *  * @since 15 九月 2018
 *  
 */
public interface DistributedReentrantLock {
    /**
     * try to get lock with given time
     *
     * @param timeout timeout
     * @param unit    time unit
     * @return <code>true</code> <code>false</code>
     * @throws InterruptedException
     */
    boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * release lock
     */
    void unlock();
}
