package org.tinygame.herostory.cmdHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.Broadcaster;
import org.tinygame.herostory.msg.GameMsgProtocol;

//用户移动指令处理器
public class UserMoveToCmdHandler implements ICmdHandler<GameMsgProtocol.UserMoveToCmd> {
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd msg) {
        //从channel上取到用户id
        Integer userId = (Integer)ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if(userId == null)
        {
            return;
        }


        //赋值
        GameMsgProtocol.UserMoveToCmd cmd = (GameMsgProtocol.UserMoveToCmd)msg;
        GameMsgProtocol.UserMoveToResult.Builder resultBuilder = GameMsgProtocol.UserMoveToResult.newBuilder();
        resultBuilder.setMoveUserId(userId);
        resultBuilder.setMoveToPosX(cmd.getMoveToPosY());
        resultBuilder.setMoveToPosY(cmd.getMoveToPosY());

        //
        GameMsgProtocol.UserMoveToResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }
}
