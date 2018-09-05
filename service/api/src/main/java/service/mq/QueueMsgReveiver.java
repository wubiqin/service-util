package service.mq;

/**
  *
  * @author biqin.wu
  * @since 18 八月 2018
  */
public interface QueueMsgReveiver {

	void receiveQueueMessage(String destination);
}
