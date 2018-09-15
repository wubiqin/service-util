package com.wbq.common.lock.zk;

import com.wbq.common.lock.CallBack;
import com.wbq.common.lock.LockTemplate;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 *  *
 *  * @author biqin.wu
 *  * @since 16 九月 2018
 *  
 */
public class ZkReentrantLockTemplate implements LockTemplate {
    private static final Logger logger = LoggerFactory.getLogger(ZkReentrantLockTemplate.class);

    private CuratorFramework client;

    public ZkReentrantLockTemplate(CuratorFramework client) {
        this.client = client;
    }

    @Override
    public Object execute(String lockId, long timeout, CallBack callBack) {
        ZkReentrantLock zkReentrantLock = new ZkReentrantLock(client, lockId);
        boolean lock = false;
        try {
            if (tryLock(zkReentrantLock, timeout)) {
                lock = true;
                return callBack.onSuccess();
            } else {
                return callBack.onTimeout();
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        } finally {
            if (lock) {
                zkReentrantLock.unlock();
            }
        }
        return null;
    }

    private boolean tryLock(ZkReentrantLock zkReentrantLock, long timeout) throws InterruptedException {
        return zkReentrantLock.tryLock(timeout, TimeUnit.MILLISECONDS);
    }
}
