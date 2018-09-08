package com.wbq.mqclient.util.netty;

import java.util.List;

import com.wbq.mqclient.util.HessianSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
  *
  * @author biqin.wu
  * @since 08 九月 2018
  */
public class NettyDecoder extends ByteToMessageDecoder {
	private Class<?> gerericClass;

	private static final int LIMIT = 4;

	public NettyDecoder(Class<?> gerericClass) {
		this.gerericClass = gerericClass;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() < LIMIT) {
			return;
		}
		in.markReaderIndex();
		int len = in.readInt();
		if (len < 0) {
			ctx.close();
		}
		if (in.readableBytes() < len) {
			// fix 1024k buffer splice limit
			in.resetReaderIndex();
			return;
		}
		byte[] bytes = new byte[len];
		in.readBytes(bytes);

		Object obj = HessianSerializer.deserialize(bytes, gerericClass);
		out.add(obj);
	}
}
