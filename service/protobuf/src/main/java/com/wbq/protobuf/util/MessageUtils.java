package com.wbq.protobuf.util;

import com.google.protobuf.Message;
import com.wbq.protobuf.parse.ParseMap;
import com.wbq.protobuf.parse.ParseRegistryMap;
import com.wbq.protobuf.proto.Internal;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 *  *
 *  * @author biqin.wu
 *  * @since 11 九月 2018
 *  
 */
public class MessageUtils {

    public static ByteBuf pack2Server(Message msg, int ptoNum, long netId, Internal.Dest dest, String userId) {
        Internal.GTransfer.Builder gtf = Internal.GTransfer.newBuilder();
        gtf.setMsg(msg.toByteString());
        gtf.setPtoNum(ptoNum);
        gtf.setNetId(netId);
        gtf.setDest(dest);
        gtf.setUserId(userId);

        byte[] bytes = gtf.build().toByteArray();
        int length = bytes.length;
        int gtfNum = ParseRegistryMap.G_TRANSFER;

        ByteBuf byteBuf = Unpooled.buffer(length + 8);
        byteBuf.writeInt(length);
        //传输协议的协议号
        byteBuf.writeInt(gtfNum);
        byteBuf.writeBytes(bytes);

        return byteBuf;
    }

    public static ByteBuf pack2Server(Message msg, int ptoNum, Internal.Dest dest, String userId) {
        Internal.GTransfer.Builder gtf = Internal.GTransfer.newBuilder();
        gtf.setMsg(msg.toByteString());
        gtf.setPtoNum(ptoNum);
        gtf.setDest(dest);
        gtf.setUserId(userId);

        byte[] bytes = gtf.build().toByteArray();
        int length = bytes.length;
        int gtfNum = ParseRegistryMap.G_TRANSFER;

        ByteBuf byteBuf = Unpooled.buffer(length + 8);
        byteBuf.writeInt(length);
        //传输协议的协议号
        byteBuf.writeInt(gtfNum);
        byteBuf.writeBytes(bytes);

        return byteBuf;
    }

    public static ByteBuf pack2Client(Message msg) {
        byte[] bytes = msg.toByteArray();
        int length = bytes.length;
        int ptoNum = ParseMap.getPtoNum(msg);

        ByteBuf byteBuf = Unpooled.buffer(length + 8);
        byteBuf.writeInt(length);
        //传输协议的协议号
        byteBuf.writeInt(ptoNum);
        byteBuf.writeBytes(bytes);

        return byteBuf;
    }
}
