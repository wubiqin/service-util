package com.wbq.mqclient.constant;

/**
  *
  * @author biqin.wu
  * @since 08 九月 2018
  */
public interface ZkConstants {
	String MQ_ZK_BASH_PATH = "/wbq-mq";

	String MQ_ZK_SERVICE_PATH = MQ_ZK_BASH_PATH.concat("/service");

	String MQ_ZK_CONSUMER_PATH = MQ_ZK_BASH_PATH.concat("/consumer");

	String ZK_CONFIG_FILE = "zk.properties";

	String ZK_ADDRESS ="zk_address";
}
