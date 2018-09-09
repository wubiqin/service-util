package com.wbq.mqclient.util.zk.queue;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.wbq.mqclient.constant.ZkConstants;
import com.wbq.mqclient.util.zk.ZkUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
  *
  * @author biqin.wu
  * @since 09 九月 2018
  */
public class ZkQueueConsumer {

	private static final Logger LOGGER = LoggerFactory.getLogger(ZkQueueConsumer.class);

	private static ZooKeeper zooKeeper;

	private static final ReentrantLock LOCK = new ReentrantLock(true);

	private static ZooKeeper getInstance() {
		if (zooKeeper == null) {
			try {
				if (LOCK.tryLock(5, TimeUnit.SECONDS)) {
					String address = ZkUtils.getZkAddress();
					CountDownLatch latch = new CountDownLatch(1);
					Watcher watcher = new ConnectedWatcher(latch);
					zooKeeper = new ZooKeeper(address, 10000, watcher);
					latch.await();

					// init base path
					Stat baseStat = zooKeeper.exists(ZkConstants.MQ_ZK_BASH_PATH, false);
					if (baseStat == null) {
						zooKeeper.create(ZkConstants.MQ_ZK_BASH_PATH, new byte[] {}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
					}

					// init consumer path
					Stat stat = zooKeeper.exists(ZkConstants.MQ_ZK_CONSUMER_PATH, false);
					if (stat == null) {
						zooKeeper.create(ZkConstants.MQ_ZK_CONSUMER_PATH, new byte[] {}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
					}
					LOGGER.info("wbq-mq zookeeper connect successful");
				}
			}
			catch (InterruptedException e) {
				LOGGER.error("fail to get lock", e);
			}
			catch (IOException e) {
				LOGGER.error("fail to instance zk");
			}
			catch (KeeperException e) {
				LOGGER.error("fail to init path", e);
			}
			finally {
				LOCK.unlock();
			}
		}
		if (zooKeeper == null) {
			throw new NullPointerException("fail to connect zookeeper");
		}
		return zooKeeper;
	}

	static final class ConnectedWatcher implements Watcher {

		private CountDownLatch countDownLatch;

		ConnectedWatcher(CountDownLatch countDownLatch) {
			this.countDownLatch = countDownLatch;
		}

		@Override
		public void process(WatchedEvent event) {
			if (event.getState() == Event.KeeperState.SyncConnected) {
				countDownLatch.countDown();
			}

		}
	}
}
