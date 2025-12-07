package com.message.center.application.strategy;

import com.message.center.domain.entity.ChannelTask;
import java.util.List;

/**
 * 渠道执行策略接口
 * 定义了渠道任务的执行方式
 */
public interface ChannelExecutionStrategy {
    /**
     * 执行渠道任务
     * @param tasks 渠道任务列表
     */
    void execute(List<ChannelTask> tasks);

    /**
     * 获取执行策略类型
     * @return 执行策略类型
     */
    ExecutionMode getExecutionMode();

    /**
     * 执行模式枚举
     */
    enum ExecutionMode {
        /** 并发执行 */
        CONCURRENT,
        /** 顺序执行 */
        SEQUENTIAL,
        /** 优先级执行 */
        PRIORITY
    }
}
