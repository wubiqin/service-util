package com.wbq.common.limiting;

import com.google.common.collect.Lists;
import com.wbq.common.file.FileUtils;
import com.wbq.common.lock.RedisPoolsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.List;

/**
 *  *
 *  * @author biqin.wu
 *  * @since 15 九月 2018
 *  
 */
public class DistributedAccessSpeedLimit extends RedisPoolsUtils {
    private static final Logger logger = LoggerFactory.getLogger(DistributedAccessSpeedLimit.class);

    private final String prefix = "limit_";

    private final String filename = "access_speed_limit.lua";

    private boolean access(String key, int seconds, int limitCount) {
        LimitRule rule = new LimitRule.Builder().seconds(seconds).limitCount(limitCount).build();
        return tryAccess(key, rule);
    }

    /**
     * for the key the max count to access per seconds
     *
     * @param key  key
     * @param rule with seconds and limitCount
     * @return <code>true</code> <code>false</code>
     */
    private boolean tryAccess(String key, LimitRule rule) {
        String realKey = prefix + key;
        Jedis jedis = getConnection();
        try {
            List<String> args = Lists.newArrayList(rule.getLimitCount() + "", rule.getSeconds() + "");
            String script = FileUtils.getScript(filename, this.getClass());
            long count = (long) jedis.eval(script, Collections.singletonList(realKey), args);
            if (count > rule.getLimitCount()) {
                logger.info("access cancel speed limit in seconds  limitCount={},seconds={},count={}", rule.getLimitCount(), rule.getSeconds(), count);
                return false;
            }
            logger.info("access success speed limit in seconds  limitCount={},seconds={},count={}", rule.getLimitCount(), rule.getSeconds(), count);
        } finally {
            releaseConnection(jedis);
        }
        return true;
    }

    public static void main(String[] args) {
        String script = FileUtils.getScript("access_speed_limit.lua", DistributedAccessSpeedLimit.class);
        System.out.println(script);
    }
}
