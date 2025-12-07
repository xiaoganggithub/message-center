package com.message.center.domain.repository;

import com.message.center.domain.entity.Message;
import com.message.center.domain.enums.MessageStatus;

/**
 * 消息仓库接口
 */
public interface MessageRepository {

    /**
     * 保存消息
     * @param message 消息对象
     * @return 是否保存成功
     */
    boolean save(Message message);

    /**
     * 根据消息ID查询消息
     * @param messageId 消息ID
     * @return 消息对象
     */
    Message getByMessageId(String messageId);

    /**
     * 更新消息状态
     * @param messageId 消息ID
     * @param status 消息状态
     * @return 是否更新成功
     */
    boolean updateStatus(String messageId, MessageStatus status);

    /**
     * 更新渠道统计信息
     * @param messageId 消息ID
     * @param successChannels 成功渠道数
     * @param failedChannels 失败渠道数
     * @return 是否更新成功
     */
    boolean updateChannelStats(String messageId, Integer successChannels, Integer failedChannels);
}