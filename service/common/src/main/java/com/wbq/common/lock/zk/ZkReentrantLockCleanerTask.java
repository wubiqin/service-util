package com.wbq.common.lock.zk;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *  * task to clean zk node
 *  * @author biqin.wu
 *  * @since 16 九月 2018
 *  
 */
public class ZkReentrantLockCleanerTask extends TimerTask {
    private static final Logger logger = LoggerFactory.getLogger(ZkReentrantLockCleanerTask.class);

    private CuratorFramework client;

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);
    /**
     * the check period unit:milliseconds
     */
    private long period = 5000;
    /**
     * max number of times to retry
     */
    private int maxRetries = 3;
    /**
     * initial amount of time to wait between retries
     */
    private final int baseSleepTimeMs = 1000;

    public ZkReentrantLockCleanerTask(String zkAdress) {
        try {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries);
            client = CuratorFrameworkFactory.newClient(zkAdress, retryPolicy);
            client.start();
            executorService.schedule(this, period, TimeUnit.MILLISECONDS);
        } catch (Throwable throwable) {
            logger.error(throwable.getMessage(), throwable);
        }
    }


    @Override
    public void run() {
        try {
            List<String> childPaths = this.client.getChildren().forPath(ZkReentrantLock.ROOT_PATH);
            childPaths.forEach(this::cleanNode);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void cleanNode(String path) {
        try {
            List<String> childPaths = this.client.getChildren().forPath(path);
            if (isEmpty(childPaths)) {
                //only can delete node that have not child in zk
                logger.info("delete path path={}", path);
                this.client.delete().forPath(path);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private boolean isEmpty(List<String> list) {
        return list == null || list.isEmpty();
    }
}
