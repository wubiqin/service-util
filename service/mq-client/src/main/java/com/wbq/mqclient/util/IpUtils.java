package com.wbq.mqclient.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
  *
  * @author biqin.wu
  * @since 06 九月 2018
  */
public class IpUtils {
	private static final Logger logger = LoggerFactory.getLogger(IpUtils.class);

	/**
	 * 获取本机ip
	 * @return ip
	 */
	public static String getIp() {
		try {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress address;
			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = networkInterfaces.nextElement();
				Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					address = addresses.nextElement();
					if (!address.isLoopbackAddress() && address.getHostAddress().contains(":")) {
						return address.getHostAddress();
					}
				}
			}
			logger.info("fail to get host address");
			return null;
		}
		catch (SocketException e) {
			logger.error("get host address error", e);
			return null;
		}
	}

	/**
	 * 获得address
	 * @param port 端口
	 * @return address
	 */
	public static String getAddress(int port) {
		String ip = getIp();
		if (ip == null) {
			return null;
		}
		return buildAddress(ip, port);
	}

	private static String buildAddress(String ip, int port) {
		return ip.concat(":").concat(String.valueOf(port));
	}
}
