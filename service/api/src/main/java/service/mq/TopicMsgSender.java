package service.mq;

/**
  *
  * @author biqin.wu
  * @since 16 八月 2018
  */
public interface TopicMsgSender {
	/**
	 * 发布信息
	 * @param destination 目的地
	 * @param message 信息
	 */
	void publishMessage(String destination, final String message);

	/**
	 *发布对象信息
	 * @param destination
	 * @param message
	 */
	void publishMessage(String destination, final Object message);
}
