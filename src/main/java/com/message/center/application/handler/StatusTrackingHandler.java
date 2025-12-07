package com.message.center.application.handler;

import com.message.center.domain.entity.Message;
import com.message.center.domain.enums.MessageStatus;
import com.message.center.domain.vo.MessageContext;
import com.message.center.domain.vo.HandlerResult;
import com.message.center.repository.MessageRepository;
import com.message.center.repository.ChannelTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 状态追踪处理器
 * 顺序：700
 * 职责：追踪各渠道的发送状态，更新消息状态
 */
@Component
public class StatusTrackingHandler implements MessageHandler {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChannelTaskRepository channelTaskRepository;

    @Override
    public HandlerResult handle(MessageContext context) {
        // 1. 更新消息状态为处理中
        messageRepository.updateStatus(context.getMessageId(), MessageStatus.PROCESSING);

        // 2. 保存渠道任务
        channelTaskRepository.saveBatch(context.getChannelTasks());

        return HandlerResult.success();
    }

    @Override
    public int getOrder() {
        return 700;
    }

    @Override
    public String getName() {
        return "状态追踪处理器";
    }

    @Override
    public boolean supports(MessageContext context) {
        return context.getChannelTasks() != null && !context.getChannelTasks().isEmpty();
    }
}
