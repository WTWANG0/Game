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
        //解码
        _msgCodeAndMsgBodyMap.put(GameMsgProtocol.MsgCode.USER_ENTRY_CMD_VALUE, GameMsgProtocol.UserEntryCmd.getDefaultInstance());
        _msgCodeAndMsgBodyMap.put(GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_CMD_VALUE, GameMsgProtocol.WhoElseIsHereCmd.getDefaultInstance());
        _msgCodeAndMsgBodyMap.put(GameMsgProtocol.MsgCode.USER_MOVE_TO_CMD_VALUE, GameMsgProtocol.UserMoveToCmd.getDefaultInstance());
        //加码
        _msgClazzAndMsgCodeMap.put(GameMsgProtocol.UserEntryResult.class,GameMsgProtocol.MsgCode.USER_ENTRY_RESULT_VALUE);
        _msgClazzAndMsgCodeMap.put(GameMsgProtocol.WhoElseIsHereResult.class,GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_RESULT_VALUE);
        _msgClazzAndMsgCodeMap.put(GameMsgProtocol.UserMoveToResult.class,GameMsgProtocol.MsgCode.USER_MOVE_TO_RESULT_VALUE);
        _msgClazzAndMsgCodeMap.put(GameMsgProtocol.UserQuitResult.class,GameMsgProtocol.MsgCode.USER_QUIT_RESULT_VALUE);
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
