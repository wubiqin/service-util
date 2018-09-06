package com.wbq.mqclient.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
  *
  * @author biqin.wu
  * @since 07 九月 2018
  */
public class JsonUtils {
	private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	/**    @see com.fasterxml.jackson.databind.ObjectMapper#writeValueAsString
	 * serial value as string
	 * @param obj val
	 * @return string
	 */
	public static String writeValueAsString(Object obj) {
		try {
			return OBJECT_MAPPER.writeValueAsString(obj);
		}
		catch (JsonProcessingException e) {
			logger.error("fail to writeValueAsString", e);
		}
		return null;
	}

	/**    @see com.fasterxml.jackson.databind.ObjectMapper#writeValueAsBytes
	 * serial value as bytes
	 * @param obj val
	 * @return byte[]
	 */
	public static byte[] writeValueAsBytes(Object obj) {
		try {
			return OBJECT_MAPPER.writeValueAsBytes(obj);
		}
		catch (JsonProcessingException e) {
			logger.error("fail to writeValueAsBytes", e);
		}
		return null;
	}

	/**    @see com.fasterxml.jackson.databind.ObjectMapper#readValue
	 * deserialize JSON content from given JSON content String
	 * @param jsonStr json content
	 * @param clazz class
	 * @param <T> type
	 * @return bean ,map,list,array
	 */
	public static <T> T readValue(String jsonStr, Class<T> clazz) {
		try {
			return OBJECT_MAPPER.readValue(jsonStr, clazz);
		}
		catch (IOException e) {
			logger.error("fail to readValue", e);
		}
		return null;
	}

	/** @see com.fasterxml.jackson.databind.ObjectMapper#readValue
	 * deserialize JSON content from given JSON content String
	 * @param jsonStr json content
	 * @param clazz class
	 * @param <T> type
	 * @return bean ,map,list,array
	 */
	public static <T> T readValueRef(String jsonStr, Class<T> clazz) {
		try {
			return OBJECT_MAPPER.readValue(jsonStr, new TypeReference<T>() {
			});
		}
		catch (IOException e) {
			logger.error("fail to readValueRef", e);
		}
		return null;
	}

	public static void main(String[] args) {
		Map<String, String> map = new HashMap<>();
		map.put("aaa", "111");
		map.put("bbb", "222");
		String json = writeValueAsString(map);
		System.out.println(json);
		Map<String, String> map1 = readValue(json, HashMap.class);
		System.out.println(map1);
	}
}
