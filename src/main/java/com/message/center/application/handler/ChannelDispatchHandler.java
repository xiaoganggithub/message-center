package com.message.center.application.handler;

import com.message.center.domain.entity.ChannelConfig;
import com.message.center.domain.entity.ChannelTask;
import com.message.center.domain.enums.TaskStatus;
import com.message.center.domain.vo.MessageContext;
import com.message.center.domain.vo.HandlerResult;
import com.message.center.infrastructure.executor.ChannelTaskExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 渠道分发处理器
 * 顺序：600
 * 职责：将消息分发到各个渠道执行
 */
@Component
public class ChannelDispatchHandler implements MessageHandler {

    @Autowired
    private ChannelTaskExecutor channelTaskExecutor;

    @Override
    public HandlerResult handle(MessageContext context) {
        // 1. 为每个渠道创建发送任务
        List<ChannelTask> tasks = new ArrayList<>();
        for (ChannelConfig config : context.getChannelConfigs()) {
            ChannelTask task = new ChannelTask();
            task.setMessageId(context.getMessageId());
            task.setChannelType(config.getChannelType());
            task.setChannelConfigId(config.getId());
            task.setRenderedContent(context.getRenderedMessages().get(config.getChannelType()));
            task.setStatus(TaskStatus.PENDING);
            task.setRetryCount(0);
            task.setMaxRetry(3); // 默认最大重试次数为3
            tasks.add(task);
        }

        // 2. 将任务设置到上下文
        context.setChannelTasks(tasks);

        // 3. 并发执行所有渠道任务
        channelTaskExecutor.executeAll(tasks);

        return HandlerResult.success();
    }

    @Override
    public int getOrder() {
        return 600;
    }

    @Override
    public String getName() {
        return "渠道分发处理器";
    }

    @Override
    public boolean supports(MessageContext context) {
        return context.getChannelConfigs() != null && !context.getChannelConfigs().isEmpty();
    }
}
