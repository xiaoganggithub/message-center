package com.message.center.domain.service;

import com.message.center.domain.entity.Message;
import com.message.center.domain.enums.MessageStatus;

/**
 * 消息领域服务接口
 */
public interface MessageDomainService {

    /**
     * 创建消息
     * @param message 消息对象
     * @return 创建后的消息
     */
    Message createMessage(Message message);

    /**
     * 更新消息状态
     * @param messageId 消息ID
     * @param status 消息状态
     * @return 是否更新成功
     */
    boolean updateMessageStatus(String messageId, MessageStatus status);

    /**
     * 更新消息渠道统计
     * @param messageId 消息ID
     * @param successChannels 成功渠道数
     * @param failedChannels 失败渠道数
     * @return 是否更新成功
     */
    boolean updateMessageChannelStats(String messageId, Integer successChannels, Integer failedChannels);

    /**
     * 完成消息处理
     * @param messageId 消息ID
     * @return 是否完成成功
     */
    boolean completeMessage(String messageId);
}