package org.tinygame.herostory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.cmdHandler.CmdHandlerFactory;

public class ServerMain {

    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(ServerMain.class);

    public static void main(String[] args) {
        CmdHandlerFactory.init();
        GameMsgRecognizer.init();
        EventLoopGroup bossGroup = new NioEventLoopGroup();     //负责客户端连接
        EventLoopGroup workerGroup = new NioEventLoopGroup();   //处理连接
        //启动辅助类
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup,workerGroup);

        b.channel(NioServerSocketChannel.class);    //服务器处理方式
        b.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(
                        new HttpServerCodec(), //http服务器编码解码器
                        new HttpObjectAggregator(65535), //内容长度限制
                        // WebSocket 协议处理器, 在这里处理握手、ping、pong 等消息
                        new WebSocketServerProtocolHandler("/websocket"),
                        new GameMsgDecoder(), // 自定义的消息解码器
                        new GameMsgEncoder(), // 自定义的消息编码器
                        new GameMsgHandler()
                );
            }
        });

        try {
            ChannelFuture f = b.bind(12345).sync();
            if (f.isSuccess()) {
                LOGGER.info("服务器启动成功!");
            }

            //等待服务器通道关闭，也就是不要退出应用程序，让程序可以一直提供服务
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
