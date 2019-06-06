package com.wbq.common.lock.redis;

import com.google.common.collect.Lists;
import com.wbq.common.file.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 *  *
 *  * @author biqin.wu
 *  * @since 15 九月 2018
 *  
 */
public class RedisLockInternals {
    private static final Logger logger = LoggerFactory.getLogger(RedisLockInternals.class);

    private JedisPool jedisPool;

    private int retryAwait = 300;
    /**
     * key expire time default one minute
     */
    private int expireTime = 60 * 30;

    private int len = 5;

    private final String lock_script = "redis_lock.lua";

    private final String unlock_script = "redis_unlock.lua";

    private final char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
            'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    public RedisLockInternals(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    /**
     * try to set
     *
     * @param key  key
     * @param time time
     * @param unit time unit
     * @return key string or null
     */
    public String tryLock(String key, long time, TimeUnit unit) {
        logger.info("try to get redis lock key={},time={}", key, time);
        final long startNano = System.nanoTime();
        final long millisToWait = (unit != null) ? unit.toMillis(time) : 0;
        String value = null;
        while (value == null) {
            value = buildKey(key, len);
            if (value != null) {
                break;
            }
            long diff = (System.nanoTime() - startNano) / 1000;
            if (diff > millisToWait) {
                break;
            }
            LockSupport.parkNanos(TimeUnit.MICROSECONDS.toNanos(retryAwait));
        }
        return value;

    }

    /**
     * del key with given value in redis
     *
     * @param key   key
     * @param value value
     */
    public void unLock(String key, String value) {
        logger.info("unlock key={} with the value={}", key, value);
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String script = FileUtils.getScript(unlock_script, this.getClass());
            long val = (long) jedis.eval(script, Collections.singletonList(key), Collections.singletonList(value));
            logger.info("del key in redis  key={},flag={}", key, val == 1);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * build redis key
     *
     * @param key key
     * @param len key suffix length
     * @return string or null
     */
    private String buildKey(String key, int len) {
        Jedis jedis = null;
        if (jedisPool == null) {
            throw new NullPointerException("jedisPool is null");
        }
        try {
            jedis = jedisPool.getResource();
            String value = key + generateId(len);
            String script = FileUtils.getScript(lock_script, this.getClass());
            List<String> args = Lists.newArrayList(value, expireTime + "");
            long count = (long) jedis.eval(script, Collections.singletonList(key), args);
            if (count == 1) {
                return value;
            }
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * generate random key suffix
     *
     * @param len suffix length
     * @return key suffix
     */
    private String generateId(int len) {
        checkParam(len);
        char[] cs = new char[len];
        for (int i = 0; i < len; i++) {
            cs[i] = digits[ThreadLocalRandom.current().nextInt(digits.length)];
        }
        String randomId = new String(cs);
        logger.info("generate randomId  randomId={}", randomId);
        return randomId;
    }

    private void checkParam(int length) {
        if (length < 1) {
            logger.error("length must > 0 length={}", length);
            throw new IllegalArgumentException("length must > 0");
        }
    }

    private boolean setnx(String key, String val, int s) {
        String res = jedisPool.getResource().set(key, val, SetParams.setParams().nx().ex(s));

        return "OK".equalsIgnoreCase(res);
    }

}
