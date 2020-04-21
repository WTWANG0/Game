package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import org.tinygame.herostory.msg.GameMsgProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public final class GameMsgRecognizer {

    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgRecognizer.class);

    //私有化类默认构造器
    private GameMsgRecognizer() {
    }

    //消息代码和消息体字典
    static private final Map<Integer, GeneratedMessageV3> _msgCodeAndMsgBodyMap = new HashMap<>();

    //消息类型和消息编号字典
    static private final Map<Class<?>, Integer> _msgClazzAndMsgCodeMap = new HashMap<>();

    //init
    public static void init() {
        //拿到GameMsgProtocol下面的所有内部类
        Class<?>[] innerClazzArray = GameMsgProtocol.class.getDeclaredClasses();
        for (Class<?> innerClazz : innerClazzArray) {
            //判断是否是GeneratedMessageV3类型
            if (!GeneratedMessageV3.class.isAssignableFrom(innerClazz)) {
                continue;
            }
            //转换类名格式
            String clazzName = innerClazz.getSimpleName();
            clazzName = clazzName.toLowerCase();

            //
            for (GameMsgProtocol.MsgCode msgCode : GameMsgProtocol.MsgCode.values()) {
                String strMsgCode = msgCode.name();
                strMsgCode = strMsgCode.replaceAll("_", "");    //去掉下划线,空格
                strMsgCode = strMsgCode.toLowerCase();          //转换成小写

                if (!strMsgCode.startsWith(clazzName)) {
                    continue;
                }

                //
                try {
                    //反射
                    Object returnObj = innerClazz.getDeclaredMethod("getDefaultInstance").invoke(innerClazz);
                    LOGGER.info("{} <==> {}", innerClazz.getName(), msgCode.getNumber());

                    _msgCodeAndMsgBodyMap.put(
                            msgCode.getNumber(),
                            (GeneratedMessageV3) returnObj
                    );

                    _msgClazzAndMsgCodeMap.put(
                            innerClazz,
                            msgCode.getNumber()
                    );
                } catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        }
    }

    //解码：根据消息编号获取构建者
    static public Message.Builder getBuilderByMsgCode(int msgCode) {
        if (msgCode < 0) {
            return null;
        }

        GeneratedMessageV3 msg = _msgCodeAndMsgBodyMap.get(msgCode);
        if (msg == null) {
            return null;
        }

        //
        return msg.newBuilderForType();
    }

    //加码：根据消息类获取消息编号
    static public int getMsgCodeByMsgClazz(Class<?> msgClazz) {
        if (msgClazz == null) {
            return -1;
        }

        Integer msgCode = _msgClazzAndMsgCodeMap.get(msgClazz);
        if (null != msgCode) {
            return msgCode.intValue();
        } else {
            return -1;
        }
    }


}
