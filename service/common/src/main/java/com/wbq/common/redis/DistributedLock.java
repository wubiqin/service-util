package com.wbq.common.redis;

import com.wbq.common.constant.RedisConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.Collections;

/**
 *  *  基于redis 实现分布式锁
 * must release jedis connection by calling {@link com.wbq.common.redis.RedisPoolsUtils#releaseConnection(Jedis jedis);}
 *  * @author biqin.wu
 *  * @since 13 九月 2018
 *  
 */
public class DistributedLock extends RedisPoolsUtils {
    private static final Logger logger = LoggerFactory.getLogger(DistributedLock.class);
    private final Jedis jedis = getConnection();

    /**
     * obtain lock
     *
     * @param key    key
     * @param value  value
     * @param expire expire time
     * @return <code>true</code> <code>false</code>
     */
    public boolean obtainLock(String key, String value, int expire) {
        logger.info("try to obtain lock in redis key={},value={},expire={}");
        if (jedis.setnx(key, value) == 1) {
            logger.info("obtain lock key={}", key);
            jedis.expire(key, expire);
            return true;
        } else {
            if (jedis.ttl(key) == RedisConstants.KEY_NO_EXIST) {
                logger.error("key does not exist key={}", key);
                return false;
            } else if (jedis.ttl(key) == -1) {
                //fail to set expire time
                jedis.expire(key, expire);
            }
        }
        logger.info("fail to obtain lock key={}", key);
        return false;
    }

    /**
     * block until obtain lock
     * {@link com.wbq.common.redis.DistributedLock#obtainLock(String key, String value, int expire)}
     *
     * @param key    key
     * @param value  value
     * @param expire expire
     * @throws InterruptedException exception
     */
    public void blockObtainLock(String key, String value, int expire) throws InterruptedException {
        for (; ; ) {
            if (obtainLock(key, value, expire)) {
                break;
            }
            //prevent from Consumption cpu
            Thread.sleep(RedisConstants.DEFAULT_SLEEP_TIME);
        }
    }

    /**
     * try obtain lock while block time >0
     *
     * @param key       key
     * @param value     value
     * @param expire    expire time
     * @param blockTime block time
     * @return <code>true</code> <code>false</code>
     * @throws InterruptedException exception
     */
    public boolean noBlockingObtainLock(String key, String value, int expire, int blockTime) throws InterruptedException {
        while (blockTime > 0) {
            boolean result = obtainLock(key, value, expire);
            if (result) {
                return true;
            }
            blockTime -= RedisConstants.DEFAULT_SLEEP_TIME;
            //prevent from Consumption cpu
            Thread.sleep(RedisConstants.DEFAULT_SLEEP_TIME);
        }
        return false;
    }

    /**
     * try obtain lock with given count
     *
     * @param key    key
     * @param value  value
     * @param expire expire time
     * @param count  try times
     * @return <code>true</code> <code>false</code>
     * @throws InterruptedException exception
     */
    public boolean tryObtainLockWithGivenCount(String key, String value, int expire, int count) throws InterruptedException {
        while (count > 0) {
            boolean result = obtainLock(key, value, expire);
            if (result) {
                return true;
            }
            count--;
            //prevent from Consumption cpu
            Thread.sleep(RedisConstants.DEFAULT_SLEEP_TIME);
        }
        return false;
    }

    /**
     * release lock with given key may occur problem that other task del the key
     *
     * @param key key
     * @return <code>true</code> <code>false</code>
     */
    public boolean releaseLock(String key) {
        logger.info("release lock key={}", key);
        return jedis.del(key) > 0;
    }

    /**
     * use lua to guarantee atomic
     *
     * @param key   key
     * @param value value
     * @return <code>true</code> <code>false</code>
     */
    public boolean releaseLock(String key, String value) {
        //lua script
        String script = "if redis.call('get', KEYS[1])==ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
        Object result = this.jedis.eval(script, Collections.singletonList(key), Collections.singletonList(value));
        return !RedisConstants.FAIL_RELEASE_LOCK.equals(result);
    }
}
