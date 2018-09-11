package com.wbq.protobuf.coder;

import com.google.protobuf.Message;
import com.wbq.protobuf.parse.ParseMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 *  *
 *  * @author biqin.wu
 *  * @since 11 九月 2018
 *  
 */
public class NettyDecoder extends ByteToMessageDecoder {
    private static final Logger logger = LoggerFactory.getLogger(NettyDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf in, List<Object> list) throws Exception {
        //mark the current readIndex
        in.markReaderIndex();
        if (in.readableBytes() < 4) {
            logger.info("the readable length less than 4 bytes (this.writerIndex - this.readerIndex) ignored");
            return;
        }
        int len = in.readInt();
        if (len < 0) {
            context.close();
            logger.error("message length less than o close channel");
            return;
        }
        if (len > in.readableBytes() - 4) {
            //编解码器in.readInt()日志，在大并发的情况下很可能会抛数组越界异常！
            in.resetReaderIndex();
            return;
        }
        int ptoNum = in.readInt();
        //Creates a new big-endian Java heap buffer , which expands its capacity boundlessly on demand.
        ByteBuf byteBuf = Unpooled.buffer(len);
        byte[] body = byteBuf.array();
        Message msg = ParseMap.getMsg(ptoNum, body);
        list.add(msg);
        logger.info("GateServer receive message: content length={},ptoNum={}", len, ptoNum);
    }
}
