package com.wbq.common.limiting;

import com.wbq.common.constant.RedisConstants;
import com.wbq.common.file.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;

import java.util.Collections;

/**
 *  * distributed service limit base on redis
 *  * @author biqin.wu
 *  * @since 15 九月 2018
 *  
 */
public abstract class AbstractRedisLimit {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected JedisCommands jedis;

    protected int limit = 20;

    protected String luaScript = FileUtils.getScript("redislimit.lua", this.getClass());

    protected abstract void buildLuaScript();

    protected AbstractRedisLimit() {
    }

    protected AbstractRedisLimit(JedisCommands jedis, String luaScript, int limit) {
        this.jedis = jedis;
        this.luaScript = luaScript;
        this.limit = limit;
    }

    protected AbstractRedisLimit(JedisCommands jedis, String luaScript) {
        this.jedis = jedis;
        this.luaScript = luaScript;
    }

    /**
     * check params
     */
    protected abstract void checkParams();

    /**
     * default achieve
     *
     * @return <code>true</code> <code>false</code>
     */
    protected boolean limit() {
        checkParams();
        String key = String.valueOf(System.currentTimeMillis() / 1000);
        Object result;
        if (jedis instanceof Jedis) {
            result = ((Jedis) this.jedis).eval(luaScript, Collections.singletonList(key), Collections.singletonList(String.valueOf(limit)));
        } else {
            logger.error("jedis is not instance of Jedis");
            //throw new RuntimeException("jedis is not instance of Jedis");
            return false;
        }
        return RedisConstants.FAIL_CODE != (long) result;
    }

    public boolean exexute() {
        checkParams();
        return limit();
    }


}
