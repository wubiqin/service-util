package com.wbq.mqclient.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
  *
  * @author biqin.wu
  * @since 08 九月 2018
  */
public class PropertiesUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesUtils.class);

	public static String getString(Properties properties, String key) {
		return properties.getProperty(key);
	}

	public static int getInt(Properties properties, String key) {
		return Integer.parseInt(properties.getProperty(key));
	}

	public static boolean getBoolean(Properties properties, String key) {
		return Boolean.parseBoolean(properties.getProperty(key));
	}

	/**
	 * load properties
	 * @param filename properties file name
	 * @return properties
	 */
	public static Properties getProperties(String filename) {
		if (Strings.isNullOrEmpty(filename)) {
			throw new IllegalArgumentException(String.format("filename can't be null or empty filename=%s", filename));
		}
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream in = loader.getResourceAsStream(filename);
		Properties properties = new Properties();
		try {
			properties.load(in);
		}
		catch (IOException e) {
			LOGGER.error("io exception", e);
		}
		return properties;
	}
}
