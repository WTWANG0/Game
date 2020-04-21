package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.cmdHandler.ICmdHandler;
import org.tinygame.herostory.cmdHandler.UserEntryCmdHandler;
import org.tinygame.herostory.cmdHandler.UserMoveToCmdHandler;
import org.tinygame.herostory.cmdHandler.WhoElseIsHereCmdHandler;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {

    //新的客户端接入
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        //添加新的客户端信道
        Broadcaster.addChannel(ctx.channel());
    }

    //客户端离开
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);

        //客户端信道数组删除对应客户端信道
        Broadcaster.removeChannel(ctx.channel());

        //拿到用户ID
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (userId == null)
            return;

        //用户字典删除对应的用户
        UserManager.removeUserById(userId);

        //
        GameMsgProtocol.UserQuitResult.Builder resultBuilder = GameMsgProtocol.UserQuitResult.newBuilder();
        resultBuilder.setQuitUserId(userId);

        //
        GameMsgProtocol.UserQuitResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }

    //处理消息
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("收到客户端消息，msgClazz" + msg.getClass().getName()+ ", msg = " + msg);
        //
        ICmdHandler<? extends GeneratedMessageV3> cmdHandler = null;
        if (msg instanceof GameMsgProtocol.UserEntryCmd) {
            cmdHandler = new UserEntryCmdHandler();
        } else if (msg instanceof GameMsgProtocol.WhoElseIsHereCmd) {
            cmdHandler = new WhoElseIsHereCmdHandler();
        } else if (msg instanceof GameMsgProtocol.UserMoveToCmd) {
            cmdHandler = new UserMoveToCmdHandler();
        }
        if(cmdHandler != null)
        {
            cmdHandler.handle(ctx,cast(msg));
        }

    }

    ///转型消息对象
    static private <TCmd extends GeneratedMessageV3> TCmd cast(Object msg) {
        if (null == msg) {
            return null;
        } else {
            return (TCmd) msg;
        }
    }
}
