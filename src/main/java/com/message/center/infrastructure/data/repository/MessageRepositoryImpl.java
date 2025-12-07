package com.message.center.infrastructure.data.repository;

import com.message.center.domain.entity.Message;
import com.message.center.domain.enums.MessageStatus;
import com.message.center.domain.repository.MessageRepository;
import com.message.center.infrastructure.data.mapper.MessageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 消息仓库实现
 */
@Repository
@Slf4j
public class MessageRepositoryImpl implements MessageRepository {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public boolean save(Message message) {
        try {
            return messageMapper.insert(message) > 0;
        } catch (Exception e) {
            log.error("保存消息失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Message getByMessageId(String messageId) {
        return messageMapper.selectByMessageId(messageId);
    }

    @Override
    public boolean updateStatus(String messageId, MessageStatus status) {
        try {
            return messageMapper.updateStatus(messageId, status) > 0;
        } catch (Exception e) {
            log.error("更新消息状态失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateChannelStats(String messageId, Integer successChannels, Integer failedChannels) {
        try {
            return messageMapper.updateChannelStats(messageId, successChannels, failedChannels) > 0;
        } catch (Exception e) {
            log.error("更新渠道统计信息失败: {}", e.getMessage(), e);
            return false;
        }
    }
}