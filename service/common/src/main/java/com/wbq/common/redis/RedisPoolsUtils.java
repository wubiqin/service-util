package com.wbq.common.redis;

import com.wbq.common.constant.RedisConstants;
import com.wbq.common.file.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ResourceBundle;

/**
 *  * redis工具类
 *  * @author biqin.wu
 *  * @since 22 八月 2018
 *  
 */
public class RedisPoolsUtils {
    private static final Logger logger = LoggerFactory.getLogger(RedisPoolsUtils.class);

    private static JedisPool jedisPool;

    private final Object lock = new Object();

    /**
     * 初始化 jedisPool
     */
    private void init() {
        logger.debug("开始初始化redis连接池");
        ResourceBundle resourceBundle = FileUtils.readResource(RedisConstants.REDIS_CONFIG_FILE);
        if (resourceBundle == null) {
            logger.error("找不到配置文件：{}.properties", RedisConstants.REDIS_CONFIG_FILE);
            throw new IllegalArgumentException("找不到redis配置文件");
        }
        //redis pool配置
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(1024);
        config.setMaxIdle(1000);
        config.setMaxWaitMillis(120000);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);

        String pwd = resourceBundle.getString(RedisConstants.REDIS_AUTH);
        String redisIp = resourceBundle.getString(RedisConstants.REDIS_IP);
        int port = Integer.parseInt(resourceBundle.getString(RedisConstants.REDIS_PORT));
        int timeout = Integer.parseInt(resourceBundle.getString(RedisConstants.REDIS_TIMEOUT));

        if ("".equals(pwd.trim())) {
            logger.info("redis 无密码连接");
            jedisPool = new JedisPool(config, redisIp, port, timeout);
        } else {
            logger.info("redis 密码连接");
            jedisPool = new JedisPool(config, redisIp, port, timeout, pwd);
        }
        logger.info("初始化redis连接池成功！");
    }

    /**
     * 获取redis连接
     *
     * @return
     */
    public Jedis getConnection() {
        if (jedisPool == null || jedisPool.isClosed()) {
            synchronized (lock) {
                if (jedisPool == null || jedisPool.isClosed()) {
                    logger.info("redis连接池为空或者redis连接池已关闭");
                    init();
                }
            }
        }
        Jedis jedis = jedisPool.getResource();
        logger.info("成功获得一个redis连接");
        return jedis;
    }

    /**
     * 释放redis连接
     *
     * @param jedis
     */
    public void releaseConnection(final Jedis jedis) {
        if (jedis != null) {
            jedis.close();
            logger.info("释放redis连接");
        }
    }

    /**
     * 关闭连接池
     */
    public void closePool() {
        jedisPool.close();
        logger.info("关闭redis连接池");
    }

}
