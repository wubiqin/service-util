package com.wbq.common.constant;

/**
 *  *
 *  * @author biqin.wu
 *  * @since 22 八月 2018
 *  
 */
public interface RedisConstants {

    /**
     * redis配置文件名称
     */
    String REDIS_CONFIG_FILE = "redis";

    // 以下是相关属性的key

    String REDIS_POOL_MAXTOTAL = "redis.pool.maxTotal";

    String REDIS_POOL_MAXIDLE = "redis.pool.maxIdle";

    String REDIS_POOL_MAXWAIT = "redis.pool.maxWait";

    String REDIS_POOL_TESTONBORROW = "redis.pool.testOnBorrow";

    String REDIS_POOL_TESTONRETURN = "redis.pool.testOnReturn";

    String REDIS_IP = "redis.ip";

    String REDIS_PORT = "redis.port";

    String REDIS_TIMEOUT = "redis.timeout";

    String REDIS_AUTH = "redis.auth";

    String REDIS_EXPIRE = "redis.expire";
    /**
     * 不存在key
     */
    int KEY_NO_EXIST =-2;

    long DEFAULT_SLEEP_TIME =1000;
}
