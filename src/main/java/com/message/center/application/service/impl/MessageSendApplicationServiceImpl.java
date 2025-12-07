package com.message.center.application.service.impl;

import com.alibaba.fastjson2.JSON;
import com.message.center.application.service.MessageSendApplicationService;
import com.message.center.domain.service.MessageDomainService;
import com.message.center.domain.vo.MessageContext;
import com.message.center.domain.vo.Message;
import com.message.center.domain.vo.SendResult;
import com.message.center.application.handler.MessageHandlerChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息发送应用服务实现
 */
@Service
public class MessageSendApplicationServiceImpl implements MessageSendApplicationService {

    private static final Logger log = LoggerFactory.getLogger(MessageSendApplicationServiceImpl.class);

    @Autowired
    private MessageDomainService messageDomainService;

    @Autowired
    private MessageHandlerChain messageHandlerChain;

    @Override
    public SendResult sendMessage(Message message) {
        try {
            // 1. 生成消息ID
            String messageId = generateMessageId();
            message.setMessageId(messageId);

            // 2. 保存消息到数据库
            com.message.center.domain.entity.Message dbMessage = convertToDbMessage(message);
            messageDomainService.createMessage(dbMessage);

            // 3. 执行责任链处理
            MessageContext context = buildMessageContext(message);
            MessageHandlerChain.ChainResult chainResult = messageHandlerChain.execute(context);

            if (chainResult.isSuccess()) {
                return SendResult.success(messageId);
            } else {
                return SendResult.fail("MESSAGE_PROCESS_ERROR", chainResult.getErrorMessage());
            }
        } catch (Exception e) {
            log.error("发送消息失败: {}", e.getMessage(), e);
            return SendResult.fail("SYSTEM_ERROR", "发送消息失败: " + e.getMessage());
        }
    }

    @Override
    public BatchSendResult batchSendMessages(List<Message> messages) {
        List<SendResult> results = new ArrayList<>();
        int successCount = 0;
        int failedCount = 0;

        for (Message message : messages) {
            SendResult result = sendMessage(message);
            results.add(result);
            if (result.isSuccess()) {
                successCount++;
            } else {
                failedCount++;
            }
        }

        return new BatchSendResult(messages.size(), successCount, failedCount, results);
    }

    /**
     * 生成消息ID
     * @return 消息ID
     */
    private String generateMessageId() {
        return "MSG" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
                + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * 将VO转换为数据库实体
     * @param message VO对象
     * @return 数据库实体
     */
    private com.message.center.domain.entity.Message convertToDbMessage(Message message) {
        com.message.center.domain.entity.Message dbMessage = new com.message.center.domain.entity.Message();
        dbMessage.setMessageId(message.getMessageId());
        dbMessage.setTenantId(message.getTenantId());
        dbMessage.setStoreId(message.getStoreId());
        dbMessage.setBusinessType(message.getBusinessType());
        dbMessage.setBusinessData(message.getBusinessData());
        dbMessage.setTargetChannels(message.getTargetChannels() != null ? JSON.toJSONString(message.getTargetChannels()) : null);
        dbMessage.setTotalChannels(message.getTargetChannels() != null ? message.getTargetChannels().size() : 0);
        return dbMessage;
    }

    /**
     * 构建消息处理上下文
     * @param message 消息对象
     * @return 消息处理上下文
     */
    private MessageContext buildMessageContext(Message message) {
        MessageContext context = new MessageContext();
        context.setMessageId(message.getMessageId());
        context.setTenantId(message.getTenantId());
        context.setStoreId(message.getStoreId());
        context.setBusinessType(message.getBusinessType());
        context.setBusinessData(message.getBusinessData());
        context.setTargetChannels(message.getTargetChannels());
        context.setStatus(MessageContext.ProcessStatus.INIT);
        return context;
    }
}