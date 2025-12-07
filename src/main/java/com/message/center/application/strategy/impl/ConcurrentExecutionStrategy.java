package com.message.center.application.strategy.impl;

import com.message.center.domain.entity.ChannelTask;
import com.message.center.infrastructure.executor.ChannelTaskExecutor;
import com.message.center.application.strategy.ChannelExecutionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 并发执行策略
 * 所有渠道异步并发执行，互不阻塞
 */
@Component
public class ConcurrentExecutionStrategy implements ChannelExecutionStrategy {

    @Autowired
    private ChannelTaskExecutor channelTaskExecutor;

    @Override
    public void execute(List<ChannelTask> tasks) {
        channelTaskExecutor.executeAll(tasks);
    }

    @Override
    public ExecutionMode getExecutionMode() {
        return ExecutionMode.CONCURRENT;
    }
}
