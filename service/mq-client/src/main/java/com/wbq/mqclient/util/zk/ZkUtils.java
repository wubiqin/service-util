package com.wbq.mqclient.util.zk;

import java.util.Properties;

import com.wbq.mqclient.constant.ZkConstants;
import com.wbq.mqclient.util.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
  *
  * @author biqin.wu
  * @since 08 九月 2018
  */
public class ZkUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZkUtils.class);
	public static String getZkAddress() {
		Properties properties = PropertiesUtils.getProperties(ZkConstants.ZK_CONFIG_FILE);
		return PropertiesUtils.getString(properties, ZkConstants.ZK_ADDRESS);
	}
}
