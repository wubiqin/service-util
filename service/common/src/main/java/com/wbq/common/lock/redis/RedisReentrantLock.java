package com.wbq.common.lock.redis;

import com.google.common.collect.Maps;
import com.wbq.common.lock.DistributedReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *  *
 *  * @author biqin.wu
 *  * @since 15 九月 2018
 *  
 */
public class RedisReentrantLock implements DistributedReentrantLock {
    private static final Logger logger = LoggerFactory.getLogger(RedisReentrantLock.class);

    private final ConcurrentMap<Thread, LockData> threadMap = Maps.newConcurrentMap();

    private JedisPool jedisPool;

    private RedisLockInternals internals;

    private String lockId;

    public RedisReentrantLock(JedisPool jedisPool, String lockId) {
        this.jedisPool = jedisPool;
        this.lockId = lockId;
        this.internals = new RedisLockInternals(jedisPool);
    }

    public RedisReentrantLock(JedisPool jedisPool, RedisLockInternals internals, String lockId) {
        this.jedisPool = jedisPool;
        this.internals = internals;
        this.lockId = lockId;
    }

    @Override
    public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
        Thread currentThread = Thread.currentThread();
        LockData lockData = threadMap.get(currentThread);
        if (lockData != null) {
            lockData.lockCount.incrementAndGet();
            logger.info("thread={} get lock and the reentrant count={} and the real key in redis :key={} ", currentThread, lockData.lockCount.incrementAndGet(), lockData.value);
            return true;
        }
        String value = internals.tryLock(lockId, timeout, unit);
        if (value != null) {
            LockData newData = new LockData(currentThread, value);
            threadMap.putIfAbsent(currentThread, newData);
            return true;
        }
        return false;
    }

    @Override
    public void unlock() {
        Thread currentThread = Thread.currentThread();
        LockData lockData = threadMap.get(currentThread);
        if (lockData == null) {
            logger.error("thread={} don't have lock", currentThread);
            throw new IllegalArgumentException("current thread don't have lock");
        }
        int count = lockData.lockCount.decrementAndGet();
        if (count > 0) {
            return;
        }
        try {
            //may throw exception in get resource from pool
            internals.unLock(lockId, lockData.value);
        } finally {
            threadMap.remove(currentThread);
        }
    }

    private static class LockData {
        final Thread ownThread;
        final String value;
        final AtomicInteger lockCount = new AtomicInteger(1);

        private LockData(Thread ownThread, String value) {
            this.ownThread = ownThread;
            this.value = value;
        }
    }


}
