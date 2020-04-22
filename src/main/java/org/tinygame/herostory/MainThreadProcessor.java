package org.tinygame.herostory;
import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.cmdHandler.CmdHandlerFactory;
import org.tinygame.herostory.cmdHandler.ICmdHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//主线程处理器
public class MainThreadProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainThreadProcessor.class);

    //构造私有化
    private MainThreadProcessor() {
    }
    //对象单例
    private static final MainThreadProcessor _instance = new MainThreadProcessor();

    //创建一个单线程
    private final ExecutorService _es = Executors.newSingleThreadExecutor((newRunnable) -> {
        Thread newThread = new Thread(newRunnable);
        newThread.setName("MainThreadProcessor");
        return newThread;
    });

    //获取单例对象
    public static MainThreadProcessor getInstance() {
        return _instance;
    }

    /**
     * 处理客户端消息
     *
     * @param ctx 客户端信道上下文
     * @param msg 消息对象
     */
    public void process(ChannelHandlerContext ctx, GeneratedMessageV3 msg) {
        if (null == ctx || null == msg) return;

        // 获取消息类
        Class<?> msgClazz = msg.getClass();

        LOGGER.info("收到客户端消息, msgClazz = {}", msgClazz.getName());

        _es.submit(() -> {
            // 获取指令处理器
            ICmdHandler<? extends GeneratedMessageV3>
                    cmdHandler = CmdHandlerFactory.create(msgClazz);

            if (null == cmdHandler) {
                LOGGER.error("未找到相对应的指令处理器, msgClazz = {}", msgClazz.getName());
                return;
            }

            try {
                /*
                注意: 这里一定要套在 try ... catch ... 块里!避免 handler 报错导致线程终止
                处理指令
                */
                cmdHandler.handle(ctx, cast(msg));

            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        });
    }


    /**
     * 转型消息对象
     *
     * @param msg    消息对象
     * @param <TCmd> 指令类型
     * @return 指令对象
     */
    static private <TCmd extends GeneratedMessageV3> TCmd cast(Object msg) {
        if (null == msg ||
                !(msg instanceof GeneratedMessageV3)) {
            return null;
        } else {
            return (TCmd) msg;
        }
    }


}
