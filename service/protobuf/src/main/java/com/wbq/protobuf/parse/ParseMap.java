package com.wbq.protobuf.parse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  *
 *  * @author biqin.wu
 *  * @since 10 九月 2018
 *  
 */
public class ParseMap {
    private static final Logger logger = LoggerFactory.getLogger(ParseMap.class);

    private static Map<Integer, Parse> parseMap = new HashMap<>();

    public static Map<Class<?>, Integer> msg2NumMap = new HashMap<>();

    @FunctionalInterface
    public interface Parse {
        /**
         * parse byte to msg
         *
         * @param bytes data
         * @return message
         * @throws IOException io exception
         */
        Message process(byte[] bytes) throws IOException;
    }

    public static void register(int ptoNum, ParseMap.Parse parse, Class<?> clazz) {
        if (parseMap.get(ptoNum) == null) {
            parseMap.put(ptoNum, parse);
        } else {
            logger.info("pto has been register in parseMap ptoNum={}", ptoNum);
            return;
        }
        if (msg2NumMap.get(clazz) == null) {
            msg2NumMap.put(clazz, ptoNum);
        } else {
            logger.info("pto has been register in msg2NumMap ptoNum={}", ptoNum);
        }
    }

    public static Message getMsg(int ptoNum, byte[] bytes) throws IOException {
        Parse parse = parseMap.get(ptoNum);
        if (parse == null) {
            logger.error("unKnow ptoNum ptoNum={}", ptoNum);
            throw new IllegalArgumentException("unKnow ptoNum");
        }
        return parse.process(bytes);
    }

    public static int getPtoNum(Message msg) {
        return getPtoNum(msg.getClass());
    }

    private static int getPtoNum(Class<?> clazz) {
        return msg2NumMap.get(clazz);
    }
}
