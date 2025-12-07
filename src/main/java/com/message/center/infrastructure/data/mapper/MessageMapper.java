package com.message.center.infrastructure.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.annotation.Select;
import com.baomidou.mybatisplus.annotation.Update;
import com.message.center.domain.entity.Message;
import com.message.center.domain.enums.MessageStatus;
import org.apache.ibatis.annotations.Param;

/**
 * 消息Mapper
 * 对应数据库表：msg_message
 */
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 根据消息ID查询消息
     * @param messageId 消息ID
     * @return 消息对象
     */
    default Message selectByMessageId(String messageId) {
        return selectOne(new LambdaQueryWrapper<Message>()
                .eq(Message::getMessageId, messageId));
    }

    /**
     * 更新消息状态
     * @param messageId 消息ID
     * @param status 消息状态
     * @return 更新结果
     */
    default int updateStatus(String messageId, MessageStatus status) {
        return update(null, new LambdaUpdateWrapper<Message>()
                .eq(Message::getMessageId, messageId)
                .set(Message::getStatus, status));
    }

    /**
     * 更新渠道统计信息
     * @param messageId 消息ID
     * @param successChannels 成功渠道数
     * @param failedChannels 失败渠道数
     * @return 更新结果
     */
    @Update("UPDATE msg_message " +
            "SET success_channels = #{successChannels}, " +
            "failed_channels = #{failedChannels}, " +
            "update_time = NOW()," +
            "status = CASE " +
            "    WHEN #{failedChannels} > 0 AND #{successChannels} = 0 THEN 'FAILED'" +
            "    WHEN #{failedChannels} > 0 AND #{successChannels} > 0 THEN 'PARTIAL_SUCCESS'" +
            "    ELSE 'SUCCESS'" +
            "END," +
            "finish_time = CASE " +
            "    WHEN #{failedChannels} > 0 AND #{successChannels} = 0 THEN NOW()" +
            "    WHEN #{failedChannels} > 0 AND #{successChannels} > 0 THEN NOW()" +
            "    ELSE NOW()" +
            "END " +
            "WHERE message_id = #{messageId}")
    int updateChannelStats(@Param("messageId") String messageId,
                          @Param("successChannels") Integer successChannels,
                          @Param("failedChannels") Integer failedChannels);
}
