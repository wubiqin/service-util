package com.wbq.mqclient.util.netty;

import com.wbq.mqclient.util.HessianSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
  *
  * @author biqin.wu
  * @since 08 九月 2018
  */
public class NettyEncoder extends MessageToByteEncoder<Object> {

	private Class<?> genericClass;

	public NettyEncoder(Class<?> genericClass) {
		this.genericClass = genericClass;
	}

	@Override
	protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
		if (genericClass.isInstance(o)) {
			byte[] date = HessianSerializer.serialize(o);
			byteBuf.writeInt(date.length);
			byteBuf.writeBytes(date);
		}
	}
}
