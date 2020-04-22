package org.tinygame.herostory.async;

/**
 * 异步操作接口
 */
public interface IAsyncOperation {
    /**
     * 获取绑定 Id
     *
     * @return 绑定 Id
     */
    default int getBindId() {
        return 0;
    }

    /**
     * 执行异步操作:IO线程处理
     */
    void doAsync();

    /**
     * 执行完成逻辑：主要线程处理
     */
    default void doFinish() {
    }
}
