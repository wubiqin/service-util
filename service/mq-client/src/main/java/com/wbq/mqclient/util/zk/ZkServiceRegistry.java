package com.wbq.mqclient.util.zk;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.collect.Sets;
import com.wbq.mqclient.constant.ZkConstants;
import com.wbq.mqclient.util.IpUtils;
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
  * @since 08 九月 2018
  */
public class ZkServiceRegistry {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZkServiceRegistry.class);

	private static ZooKeeper zooKeeper;

	private static final ReentrantLock LOCK = new ReentrantLock(true);

	/**
	 * registry service
	 * @param port port
	 * @param registryKeySet key
	 * @throws KeeperException KeeperException
	 * @throws InterruptedException InterruptedException
	 */
	public static void registryService(int port, Set<String> registryKeySet) throws KeeperException, InterruptedException {
		checkPortAndRegistryKeySet(port, registryKeySet);
		String address = IpUtils.getAddress(port);
		if (address == null) {
			throw new NullPointerException("address is null");
		}
		for (String registryKey : registryKeySet) {
			String registryKeyAddressPath = registryPath(address, registryKey);
			LOGGER.info("wbq-mq registry service item, registryKey:{}, address:{}, registryKeyAddressPath:{}", registryKey, address, registryKeyAddressPath);
		}

	}

	private static void checkPortAndRegistryKeySet(int port, Set<String> registryKeySet) {
		if (port < 0) {
			LOGGER.error("illegal port={}", port);
			throw new IllegalArgumentException("illegal port");
		}
		if (registryKeySet == null || registryKeySet.size() == 0) {
			LOGGER.error("illegal registryKeySet={}", registryKeySet);
			throw new IllegalArgumentException("illegal registryKeySet");
		}
	}

	private static String registryPath(String address, String registryKey) throws KeeperException, InterruptedException {
		//registry registryKeyPath
		String registryKeyPath = ZkConstants.MQ_ZK_SERVICE_PATH.concat("/").concat(registryKey);
		Stat registryKeyStat = getInstance().exists(registryKeyPath, false);
		if (registryKeyStat == null) {
			getInstance().create(registryKeyPath, new byte[] {}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		//registry registryKeyPathAddress
		String registryKeyAddressPath = registryKeyPath.concat("/").concat(address);
		Stat registryKeyAddressStat = getInstance().exists(registryKeyAddressPath, false);
		if (registryKeyAddressStat == null) {
			getInstance().create(registryKeyAddressPath, new byte[] {}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		return registryKeyAddressPath;
	}

	/**
	 * instance zk
	 * @return zk instance
	 */
	private static ZooKeeper getInstance() {
		if (zooKeeper == null) {
			try {
				String address = ZkUtils.getZkAddress();
				if (LOCK.tryLock(5, TimeUnit.SECONDS)) {
					initZk(address);
					initStat();
					LOGGER.info("wbq-mq connect zookeeper successful");
				}
			}
			catch (InterruptedException e) {
				LOGGER.error("fail to get lock", e);
			}
			catch (IOException e) {
				LOGGER.error("fail to new zookeeper ", e);
			}
			catch (KeeperException e) {
				LOGGER.error("fail to get stat", e);
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

	/**
	 * init bash path and service path in zk
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	private static void initStat() throws KeeperException, InterruptedException {
		Stat baseStat = getInstance().exists(ZkConstants.MQ_ZK_BASH_PATH, false);
		LOGGER.info("init base path");
		if (baseStat == null) {
			getInstance().create(ZkConstants.MQ_ZK_BASH_PATH, new byte[] {}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		LOGGER.info("init service path");
		Stat serviceStat = getInstance().exists(ZkConstants.MQ_ZK_SERVICE_PATH, false);
		if (serviceStat == null) {
			getInstance().create(ZkConstants.MQ_ZK_SERVICE_PATH, new byte[] {}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
	}

	/**
	 * check zookeeper stat
	 * @param address address
	 * @throws IOException exception
	 */
	private static void initZk(String address) throws IOException, InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		Watcher watcher = new ConnectedWatcher(latch);
		zooKeeper = new ZooKeeper(address, 10000, watcher);
		latch.await();
	}

	static class ConnectedWatcher implements Watcher {
		private CountDownLatch latch;

		ConnectedWatcher(CountDownLatch latch) {
			this.latch = latch;
		}

		@Override
		public void process(WatchedEvent event) {
			if (event.getState() == Watcher.Event.KeeperState.Expired) {
				try {
					zooKeeper.close();
				}
				catch (InterruptedException e) {
					LOGGER.error("fail to close zookeeper", e);
				}
				zooKeeper = null;
			}
			if (event.getState() == Event.KeeperState.SyncConnected) {
				latch.countDown();
			}
		}

	}

	public static void main(String[] args) throws KeeperException, InterruptedException {
		registryService(3333, Sets.newHashSet("path2"));
	}
}
