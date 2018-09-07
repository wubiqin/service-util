package com.wbq.mqclient.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
  *
  * @author biqin.wu
  * @since 08 九月 2018
  */
public class DateFormatUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(DateFormatUtils.class);

	private static final ConcurrentHashMap<String, ThreadLocal<SimpleDateFormat>> SDF_MAP = new ConcurrentHashMap<>(256);

	private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

	/**
	 *  format date to string
	 * @param date date
	 * @return date string
	 */
	public static String formatDate(Date date) {
		SimpleDateFormat sdf = getSdf();
		return sdf.format(date);
	}

	public static String formatDateWithPattern(Date date, String pattern) {
		SimpleDateFormat sdf = getSdf(pattern);
		return sdf.format(date);
	}

	/**
	 * parse data string to date
	 * @param data date string
	 * @return date
	 */
	public static Date parse(String data) {
		SimpleDateFormat sdf = getSdf();
		try {
			return sdf.parse(data);
		}
		catch (ParseException e) {
			LOGGER.error("fail to parse {} to date", data, e);
		}
		return null;
	}

	public static Date parseWithPattern(String data, String pattern) {
		SimpleDateFormat sdf = getSdf(pattern);
		try {
			return sdf.parse(data);
		}
		catch (ParseException e) {
			LOGGER.error("fail to parse {} to date", data, e);
		}
		return null;
	}

	private static SimpleDateFormat getSdf() {
		return getSdf(DEFAULT_PATTERN);
	}

	private static SimpleDateFormat getSdf(String pattern) {
		if (Strings.isNullOrEmpty(pattern)) {
			throw new IllegalArgumentException("pattern can't be null or empty");
		}
		ThreadLocal<SimpleDateFormat> sdfHolder = SDF_MAP.get(pattern);
		if (sdfHolder == null) {
			sdfHolder = ThreadLocal.withInitial(() -> new SimpleDateFormat(pattern));
			SDF_MAP.put(pattern, sdfHolder);
		}
		return sdfHolder.get();
	}

}
