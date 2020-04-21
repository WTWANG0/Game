package org.tinygame.herostory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
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
        if (msg instanceof GameMsgProtocol.UserEntryCmd) {
            //从msg中拿到个人信息
            GameMsgProtocol.UserEntryCmd cmd = (GameMsgProtocol.UserEntryCmd) msg;
            int userId = cmd.getUserId();
            String heroAvatar = cmd.getHeroAvatar();
            
            //
            GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
            resultBuilder.setUserId(userId);
            resultBuilder.setHeroAvatar(heroAvatar);

            // 将用户加入字典
            User newUser = new User();
            newUser.userId = userId;
            newUser.heroAvatar = heroAvatar;
            UserManager.addUser(newUser );

            //将用户 Id 附着到 Channel
            ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);
            
            //构建结果并发送
            GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();
            //群发
            Broadcaster.broadcast(newResult);
        } else if (msg instanceof GameMsgProtocol.WhoElseIsHereCmd) {
            GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();
            for (User currUser : UserManager.listUser()) {
                if (null == currUser) {
                    continue;
                }

                GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder userInfoBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
                userInfoBuilder.setUserId(currUser.userId);
                userInfoBuilder.setHeroAvatar(currUser.heroAvatar);
                //
                resultBuilder.addUserInfo(userInfoBuilder);
            }
            GameMsgProtocol.WhoElseIsHereResult newResult = resultBuilder.build();
            //谁请求给发
            ctx.writeAndFlush(newResult);
        } else if (msg instanceof GameMsgProtocol.UserMoveToCmd) {
            //从channel上取到用户id
            Integer userId = (Integer)ctx.channel().attr(AttributeKey.valueOf("userId")).get();
            if(userId == null)
                return;

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
}
