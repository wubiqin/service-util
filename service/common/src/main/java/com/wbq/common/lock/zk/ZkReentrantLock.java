package com.wbq.common.lock.zk;

import com.wbq.common.lock.DistributedReentrantLock;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *  * reentrant lock base on zookeeper (only suitable for which have the zk lock in the jvm)
 *  * @author biqin.wu
 *  * @since 16 九月 2018
 *  
 */
public class ZkReentrantLock implements DistributedReentrantLock {
    private static final Logger logger = LoggerFactory.getLogger(ZkReentrantLock.class);

    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);
    /**
     * the root of all the persistent node
     */
    public static final String ROOT_PATH = "/root_lock/";
    /**
     * the time cycle to clean persistent node unit:milliseconds
     */
    private long delayToClean = 1000;
    /**
     * share lock base on zk
     */
    private InterProcessMutex interProcessMutex;
    /**
     * the persistent node
     */
    private String path;
    /**
     * the client of zk
     */
    private CuratorFramework client;

    public ZkReentrantLock(CuratorFramework client, String lockId) {
        init(client, lockId);
    }

    private void init(CuratorFramework client, String lockId) {
        this.client = client;
        this.path = ROOT_PATH + lockId;
        this.interProcessMutex = new InterProcessMutex(client, path);
    }

    @Override
    public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
        try {
            return interProcessMutex.acquire(timeout, unit);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            executor.schedule(new Cleaner(client, path), delayToClean, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void unlock() {
        try {
            interProcessMutex.release();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static class Cleaner implements Runnable {

        CuratorFramework client;
        String path;

        private Cleaner(CuratorFramework client, String path) {
            this.client = client;
            this.path = path;
        }

        @Override
        public void run() {
            try {
                List<String> pathList = client.getChildren().forPath(path);
                if (pathList == null || pathList.isEmpty()) {
                    //the path can be delete if it has no child
                    client.delete().forPath(path);
                }
            } catch (Exception e) {
                //delete when other create lock so throw exception
                logger.error(e.getMessage(), e);
            }
        }
    }
}
