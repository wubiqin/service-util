package com.wbq.protobuf.parse;

import com.wbq.protobuf.proto.Auth;
import com.wbq.protobuf.proto.Chat;
import com.wbq.protobuf.proto.Internal;

/**
 *  *
 *  * @author biqin.wu
 *  * @since 11 九月 2018
 *  
 */
public class ParseRegistryMap {

    public static final int G_TRANSFER = 900;

    public static final int GREEN = 901;

    public static final int C_LOGIN = 1000;

    public static final int C_REGISTER = 1001;

    public static final int S_RESPONSE = 1002;

    public static final int C_PRIVATE_CHAT = 1003;

    public static final int S_PRIVATE_CHAT = 1004;

    public static void initRegistry() {
        ParseMap.register(G_TRANSFER, Internal.GTransfer::parseFrom, Internal.GTransfer.class);
        ParseMap.register(GREEN, Internal.Greet::parseFrom, Internal.Greet.class);
        ParseMap.register(C_LOGIN, Auth.CLogin::parseFrom, Auth.CLogin.class);
        ParseMap.register(C_REGISTER, Auth.CRegister::parseFrom, Auth.CRegister.class);
        ParseMap.register(S_RESPONSE, Auth.SResponse::parseFrom, Auth.SResponse.class);
        ParseMap.register(C_PRIVATE_CHAT, Chat.CPrivateChat::parseFrom, Chat.CPrivateChat.class);
        ParseMap.register(S_PRIVATE_CHAT, Chat.SPrivateChat::parseFrom, Chat.SPrivateChat.class);
    }

}
