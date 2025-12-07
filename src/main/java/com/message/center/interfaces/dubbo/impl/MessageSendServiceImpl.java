package com.message.center.interfaces.dubbo.impl;

import com.apache.dubbo.config.annotation.DubboService;
import com.message.center.application.service.MessageSendApplicationService;
import com.message.center.domain.vo.Message;
import com.message.center.domain.vo.SendResult;
import com.message.center.interfaces.dubbo.api.MessageSendService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 消息发送Dubbo服务实现
 */
@DubboService
public class MessageSendServiceImpl implements MessageSendService {

    @Autowired
    private MessageSendApplicationService messageSendApplicationService;

    @Override
    public SendResult sendMessage(Message message) {
        return messageSendApplicationService.sendMessage(message);
    }

    @Override
    public BatchSendResult batchSendMessages(List<Message> messages) {
        MessageSendApplicationService.BatchSendResult appResult = messageSendApplicationService.batchSendMessages(messages);
        return new BatchSendResult(appResult.getTotalCount(), appResult.getSuccessCount(), appResult.getFailedCount(), appResult.getResults());
    }
}