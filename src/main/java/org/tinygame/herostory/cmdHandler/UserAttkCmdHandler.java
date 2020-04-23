package org.tinygame.herostory.cmdHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.Broadcaster;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.mq.MQProducer;
import org.tinygame.herostory.mq.VictorMsg;
import org.tinygame.herostory.msg.GameMsgProtocol;

public class UserAttkCmdHandler implements ICmdHandler<GameMsgProtocol.UserAttkCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserAttkCmd cmd) {
        if (null == ctx ||
                null == cmd) {
            return;
        }

        // 获取攻击者 Id
        Integer attkUserId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (null == attkUserId) {
            return;
        }

        // 获取被攻击者 Id
        int targetUserId = cmd.getTargetUserId();

        GameMsgProtocol.UserAttkResult.Builder resultBuilder = GameMsgProtocol.UserAttkResult.newBuilder();
        resultBuilder.setAttkUserId(attkUserId);
        resultBuilder.setTargetUserId(targetUserId);

        GameMsgProtocol.UserAttkResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);

        //减血操作
        User targetUser = UserManager.getUserById(targetUserId);
        if (targetUser == null) return;

        //
        int subtractHp = 10;
        targetUser.currHp = targetUser.currHp - subtractHp;
        // 广播减血消息
        broadcastSubtractHp(targetUserId, subtractHp);

        if (targetUser.currHp <= 0) {
            // 广播死亡消息
            broadcastDie(targetUserId);

            if (!targetUser.died) {
                // 设置死亡标志
                targetUser.died = true;
                // 发送消息到 MQ
                VictorMsg mqMsg = new VictorMsg();
                mqMsg.winnerId = attkUserId;
                mqMsg.loserId = targetUserId;
                MQProducer.sendMsg("Victor", mqMsg);

            }
        }
    }

    /**
     * 广播减血消息
     * @param targetUserId 被攻击者 Id
     * @param subtractHp   减血量
     */
    static private void broadcastSubtractHp(int targetUserId, int subtractHp) {
        if (targetUserId <= 0 ||
                subtractHp <= 0) {
            return;
        }

        GameMsgProtocol.UserSubtractHpResult.Builder resultBuilder = GameMsgProtocol.UserSubtractHpResult.newBuilder();
        resultBuilder.setTargetUserId(targetUserId);
        resultBuilder.setSubtractHp(subtractHp);

        GameMsgProtocol.UserSubtractHpResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }


    //广播死亡消息
    //targetUserId 被攻击者 Id
    static private void broadcastDie(int targetUserId) {
        if (targetUserId <= 0) return;

        GameMsgProtocol.UserDieResult.Builder resultBuilder = GameMsgProtocol.UserDieResult.newBuilder();
        resultBuilder.setTargetUserId(targetUserId);

        GameMsgProtocol.UserDieResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }



}
