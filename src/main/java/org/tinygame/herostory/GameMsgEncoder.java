package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.msg.GameMsgProtocol;

/*
* 消息编码
* */
public class GameMsgEncoder extends ChannelOutboundHandlerAdapter {
    //日志对象
    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgEncoder.class);

    //
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg == null || !(msg instanceof GeneratedMessageV3)) {
            super.write(ctx, msg, promise);
            return;
        }

        int msgCode =GameMsgRecognizer.getMsgCodeByMsgClazz(msg);
        if(msgCode <= -1){
            LOGGER.error("无法识别的消息, msgClazz = {}", msg.getClass().getName());
            return;
        }

        byte[] msgBody = ((GeneratedMessageV3)msg).toByteArray();

        ByteBuf byteBuf = ctx.alloc().buffer(); //申请一个ByteBuf
        byteBuf.writeShort((short)0);           // 写出消息长度, 目前写出 0 只是为了占位
        byteBuf.writeShort((short)msgCode);     // 写出消息编号
        byteBuf.writeBytes(msgBody);            // 写出消息体

        BinaryWebSocketFrame frame = new BinaryWebSocketFrame(byteBuf);
        super.write(ctx,frame,promise);
    }
}
