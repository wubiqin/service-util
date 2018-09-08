package com.wbq.mqclient.util.zk;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wbq.mqclient.util.PropertiesUtils;
import com.wbq.mqclient.util.com.wbq.mqclient.constant.ZkConstants;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
  *
  * @author biqin.wu
  * @since 08 九月 2018
  */
public class ZkServiceDiscovery {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZkServiceDiscovery.class);

	private static ZooKeeper zooKeeper;

	private static final ReentrantLock LOCK = new ReentrantLock(true);

	private static final ConcurrentMap<String, Set<String>> ADDRESS_MAP = new ConcurrentHashMap<>();

	private static ThreadPoolExecutor getExecutor() {
		return new ThreadPoolExecutor(5, 5, 200, TimeUnit.SECONDS,
				new LinkedBlockingDeque<>(1000), new ThreadFactoryBuilder().setNameFormat("wbq-thread-%d").build());
	}

	/**
	 * discover service every 60s with five thread
	 */
	static {
		getExecutor().execute(() -> {
			while (true) {
				try {
					TimeUnit.SECONDS.sleep(60L);
					discoverService();
				}
				catch (InterruptedException e) {
					LOGGER.error("fail to sleep thread");
				}
			}
		});
	}

	/**
	 * get zk address
	 * @param registerKey key
	 * @return address
	 */
	public static String discover(String registerKey) {
		Set<String> addressSet = ADDRESS_MAP.get(registerKey);
		if (addressSet == null) {
			ADDRESS_MAP.put(registerKey, Sets.newHashSet());
			discoverService();
			addressSet = ADDRESS_MAP.get(registerKey);
		}
		if (addressSet.size() == 0) {
			return null;
		}
		List<String> addressList = Lists.newArrayList(addressSet);
		if (addressList.size() == 1) {
			return addressList.get(0);
		}
		else {
			return addressList.get(new Random().nextInt(addressList.size()));
		}
	}

	/**
	 * get zookeeper
	 * @return zookeeper instance
	 */
	private static ZooKeeper getInstance() {
		if (zooKeeper == null) {
			try {
				if (LOCK.tryLock(5, TimeUnit.SECONDS)) {
					Properties properties = PropertiesUtils.getProperties(ZkConstants.ZK_CONFIG_FILE);
					String address = PropertiesUtils.getString(properties, ZkConstants.ZK_ADDRESS);
					try {
						zooKeeper = new ZooKeeper(address, 10000, event -> {
							//session expire create new
							if (event.getState() == Watcher.Event.KeeperState.Expired) {
								try {
									zooKeeper.close();
								}
								catch (InterruptedException e) {
									LOGGER.error("fail to close zk", e);
								}
								zooKeeper = null;
							}

							LOGGER.info("refresh service address", event);
							if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
								if (event.getPath() != null && event.getPath().startsWith(ZkConstants.MQ_ZK_SERVICE_PATH)) {
									discoverService();
								}
							}
							else if (event.getType() == Watcher.Event.EventType.None) {
								discoverService();
							}
						});
						LOGGER.info("wbq-mq zookeeper connect successful");
					}
					catch (IOException e) {
						LOGGER.error("io exception ", e);
					}
					finally {
						LOCK.unlock();
					}
				}
			}
			catch (InterruptedException e) {
				LOGGER.error("InterruptedException ", e);
			}
		}
		if (zooKeeper == null) {
			throw new NullPointerException("fail to connect zookeeper");
		}
		return zooKeeper;
	}

	/**
	 * discovery service and add it to the map
	 */
	private static void discoverService() {
		if (ADDRESS_MAP.size() == 0) {
			return;
		}
		for (String registerKey : ADDRESS_MAP.keySet()) {
			Set<String> addressSet = Sets.newHashSet();
			String registerKeyPath = ZkConstants.MQ_ZK_SERVICE_PATH.concat("/").concat(registerKey);
			try {
				Stat stat = getInstance().exists(registerKeyPath, true);
				if (stat != null) {
					List<String> addressList = getInstance().getChildren(registerKeyPath, true);
					if (addressList != null && addressList.size() > 0) {
						addressSet.addAll(addressList);
					}
				}
				ADDRESS_MAP.put(registerKey, addressSet);
				LOGGER.info("wbq-mq discover service registerKey={},addressSet={}", registerKey, addressSet);
			}
			catch (KeeperException | InterruptedException e) {
				LOGGER.error("fail to discover service", e);
			}
		}
	}
}
