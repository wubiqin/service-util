package com.wbq.common.limiting;

import com.google.common.util.concurrent.RateLimiter;
import com.wbq.common.file.FileUtils;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 *  *
 *  * @author biqin.wu
 *  * @since 14 九月 2018
 *  
 */
public class SingleLimitTest {

    @Test
    public void limit() {
        RateLimiter limiter = SingleLimit.limit(3.0);
        for (int i = 0; i < 10; i++) {
            double acquire = limiter.acquire();
            System.out.println("time "+new Date()+"success to acquire token acquire= " + acquire);
        }
    }

    @Test
    public void getScript(){
        String script= FileUtils.getScript("redislimit.lua",FileUtils.class);
        System.out.println(script);
    }
}