package com.wbq.common.lock.redis;

import com.wbq.common.lock.CallBack;
import com.wbq.common.lock.LockTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.TimeUnit;

/**
 *  *
 *  * @author biqin.wu
 *  * @since 16 九月 2018
 *  
 */
public class RedisDistributedLockTemplate implements LockTemplate {
    private static final Logger logger = LoggerFactory.getLogger(RedisDistributedLockTemplate.class);

    private JedisPool jedisPool;

    public RedisDistributedLockTemplate(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public Object execute(String lockId, long timeout, CallBack callBack) {
        RedisReentrantLock redisReentrantLock = null;
        boolean lock = false;
        try {
            redisReentrantLock = new RedisReentrantLock(jedisPool, lockId);
            if (redisReentrantLock.tryLock(timeout, TimeUnit.MILLISECONDS)) {
                lock = true;
                return callBack.onSuccess();
            } else {
                return callBack.onTimeout();
            }
        } catch (Exception e) {
            logger.error("fail when execute", e);
        } finally {
            if (lock) {
                redisReentrantLock.unlock();
            }
        }
        return null;
    }
}
