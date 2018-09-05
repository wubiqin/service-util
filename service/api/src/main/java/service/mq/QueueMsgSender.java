package service.mq;

/**
  *
  * @author biqin.wu
  * @since 16 八月 2018
  */
public interface QueueMsgSender {
	/**
	 * 发送文本信息
	 * @param destination 目的地
	 * @param message 信息
	 */
	void sendMessage(String destination, final String message);

	/**
	 * 发送对象信息
	 * @param destination
	 * @param message
	 */
	void sendMessage(String destination, final Object message);
}
