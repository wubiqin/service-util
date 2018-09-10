package com.wbq.protobuf.coder;

import com.google.protobuf.Message;
import com.wbq.protobuf.parse.ParseMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  *
 *  * @author biqin.wu
 *  * @since 11 九月 2018
 *  
 */
public class NettyEncoder extends MessageToByteEncoder<Message> {
    private static final Logger logger = LoggerFactory.getLogger(NettyEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext context, Message msg, ByteBuf out) throws Exception {
        byte[] bytes = msg.toByteArray();
        int ptoNum = ParseMap.getPtoNum(msg);
        int len = bytes.length;
        ByteBuf byteBuf = Unpooled.buffer(len + 8);
        byteBuf.writeInt(len);
        byteBuf.writeInt(ptoNum);
        byteBuf.writeBytes(bytes);

        out.writeBytes(byteBuf);

        logger.info("GateServer send message ,remoteAddress={},content length={},ptoNum={}", context.channel().remoteAddress(), len, ptoNum);
    }
}
