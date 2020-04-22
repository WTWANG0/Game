package org.tinygame.herostory.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.MainThreadProcessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AsyncOperationProcessor {

    static private final Logger LOGGER = LoggerFactory.getLogger(AsyncOperationProcessor.class);

    private AsyncOperationProcessor(){}

    //单例
    private static  final AsyncOperationProcessor _instance = new AsyncOperationProcessor();

    //获取单例对象 @return 异步操作处理器
    static public AsyncOperationProcessor getInstance() {
        return _instance;
    }

    //
    private final ExecutorService _es = Executors.newSingleThreadExecutor((newRunnable) -> {
        Thread newThread = new Thread(newRunnable);
        newThread.setName("AsyncOperationProcessor");
        return newThread;
    });

    //处理异步惭怍
    public void process(IAsyncOperation asyOp) {
        if (asyOp == null) return;

        _es.submit(() -> {
            try {
                //异步执行doAsync
                asyOp.doAsync();

                //返回主线程执行doFinish操作
                MainThreadProcessor.getInstance().process(() -> {
                    asyOp.doFinish();
                });

            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        });
    }

}
