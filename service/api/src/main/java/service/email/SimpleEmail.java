package service.email;

import java.io.File;

/**
  *
  * @author biqin.wu
  * @since 19 八月 2018
  */
public interface SimpleEmail {
	/**
	 * 发送邮件给用户
	 * @param mailTo 账号
	 * @param subject 主题
	 * @param content 内容
	 * @return
	 */
	boolean sendEmailToUser(String mailTo, String subject, String content);

	/**
	 * 带附件的邮件
	 * @param mailTo
	 * @param subject
	 * @param content
	 * @param attachFile
	 * @return
	 */
	boolean sendEmailToUser(String mailTo, String subject, String content, File attachFile);
}
