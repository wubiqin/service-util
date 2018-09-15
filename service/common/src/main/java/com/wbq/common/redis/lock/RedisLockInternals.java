package com.wbq.common.redis.lock;

import com.wbq.common.file.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;
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

    private final String filename = "redis_lock.lua";

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
     * @param key
     * @param time
     * @param unit
     * @return
     */
    public boolean tryLock(String key, long time, TimeUnit unit) {
        final long startMillis = System.nanoTime() / 1000;
        final long millisToWait = (unit != null) ? unit.toMillis(time) : 0;
        String value = null;
        while (value == null) {
            value = buildKey(key, len);
            if (value != null) {
                break;
            }
            long diff = System.currentTimeMillis() - startMillis;
            if (diff > millisToWait) {
                break;
            }
            LockSupport.parkNanos(TimeUnit.MICROSECONDS.toNanos(retryAwait));
        }
        return value != null;

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
            String redisKey = key + generateId(len);
            String script = FileUtils.getScript(filename, this.getClass());
            long count = (long) jedis.eval(script, Collections.singletonList(redisKey), Collections.singletonList(expireTime + ""));
            if (count == 1) {
                return redisKey;
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
        return new String(cs);
    }

    private void checkParam(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("length must > 0");
        }
    }

}
