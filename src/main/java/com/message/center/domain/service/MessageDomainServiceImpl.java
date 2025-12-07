package com.message.center.domain.service.impl;

import com.message.center.domain.entity.Message;
import com.message.center.domain.enums.MessageStatus;
import com.message.center.domain.repository.MessageRepository;
import com.message.center.domain.service.MessageDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 消息领域服务实现
 */
@Service
public class MessageDomainServiceImpl implements MessageDomainService {

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public Message createMessage(Message message) {
        message.setStatus(MessageStatus.PENDING);
        message.setCreateTime(LocalDateTime.now());
        message.setUpdateTime(LocalDateTime.now());
        message.setSuccessChannels(0);
        message.setFailedChannels(0);
        messageRepository.save(message);
        return message;
    }

    @Override
    public boolean updateMessageStatus(String messageId, MessageStatus status) {
        return messageRepository.updateStatus(messageId, status);
    }

    @Override
    public boolean updateMessageChannelStats(String messageId, Integer successChannels, Integer failedChannels) {
        return messageRepository.updateChannelStats(messageId, successChannels, failedChannels);
    }

    @Override
    public boolean completeMessage(String messageId) {
        Message message = messageRepository.getByMessageId(messageId);
        if (message != null) {
            // 根据成功和失败渠道数确定最终状态
            MessageStatus finalStatus;
            if (message.getFailedChannels() == 0) {
                finalStatus = MessageStatus.SUCCESS;
            } else if (message.getSuccessChannels() == 0) {
                finalStatus = MessageStatus.FAILED;
            } else {
                finalStatus = MessageStatus.PARTIAL_SUCCESS;
            }
            
            boolean statusUpdated = messageRepository.updateStatus(messageId, finalStatus);
            return statusUpdated;
        }
        return false;
    }
}