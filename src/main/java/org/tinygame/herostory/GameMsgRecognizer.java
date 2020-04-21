package org.tinygame.herostory;

import com.google.protobuf.Message;
import org.tinygame.herostory.msg.GameMsgProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GameMsgRecognizer {

    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgRecognizer.class);
    //私有化类默认构造器
    private GameMsgRecognizer() { }

    static public Message.Builder getBuilderByMsgCode(int msgCode) {
        switch (msgCode) {
            case GameMsgProtocol.MsgCode.USER_ENTRY_CMD_VALUE:
                return GameMsgProtocol.UserEntryCmd.newBuilder();
            case GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_CMD_VALUE:
                return GameMsgProtocol.WhoElseIsHereCmd.newBuilder();
            case GameMsgProtocol.MsgCode.USER_MOVE_TO_CMD_VALUE:
                return GameMsgProtocol.UserMoveToCmd.newBuilder();
            default:
                return null;
        }
    }

    //加码：根据消息类获取消息编号
    static public int getMsgCodeByMsgClazz(Object msg) {
        if (msg instanceof GameMsgProtocol.UserEntryResult) {
            return GameMsgProtocol.MsgCode.USER_ENTRY_RESULT_VALUE;
        } else if (msg instanceof GameMsgProtocol.WhoElseIsHereResult) {
            return GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_RESULT_VALUE;
        } else if (msg instanceof GameMsgProtocol.UserMoveToResult) {
            return GameMsgProtocol.MsgCode.USER_MOVE_TO_RESULT_VALUE;
        } else if (msg instanceof GameMsgProtocol.UserQuitResult) {
            return GameMsgProtocol.MsgCode.USER_QUIT_RESULT_VALUE;
        } else {
            LOGGER.error("无法识别的消息类型, msgClazz = " + msg.getClass().getName());
            return -1;
        }
    }


}
