package com.wbq.mqclient.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
  *
  * @author biqin.wu
  * @since 08 九月 2018
  */
public class HessianSerializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(HessianSerializer.class);

	/**
	 * serial object to bytes
	 * @param obj obj
	 * @param <T> obj type
	 * @return bytes
	 */
	public static <T> byte[] serialize(T obj) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		HessianOutput ho = new HessianOutput(out);
		try {
			ho.writeObject(obj);
		}
		catch (IOException e) {
			LOGGER.error("io exception");
		}
		return out.toByteArray();
	}

	/**
	 * Reads an object from the input stream with an expected type.
	 * @param bytes bytes
	 * @param clazz type
	 * @param <T> type
	 * @return obj
	 */
	public static <T> Object deserialize(byte[] bytes, Class<T> clazz) {
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		HessianInput hi = new HessianInput(in);
		try {
			return hi.readObject(clazz);
		}
		catch (IOException e) {
			LOGGER.error("io exception", e);
			throw new RuntimeException(e);
		}
	}

	public static Object deserialize(byte[] bytes) {
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		HessianInput hi = new HessianInput(in);
		try {
			return hi.readObject();
		}
		catch (IOException e) {
			LOGGER.error("io exception", e);
			throw new RuntimeException(e);
		}
	}
}
