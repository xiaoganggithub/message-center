package com.message.center.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.message.center.domain.enums.ChannelType;
import com.message.center.domain.enums.TaskStatus;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 渠道发送任务表
 * 对应数据库表：msg_channel_task
 */
@Data
@TableName("msg_channel_task")
public class ChannelTask implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 消息ID
     */
    @TableField("message_id")
    private String messageId;

    /**
     * 渠道类型
     */
    @TableField("channel_type")
    private ChannelType channelType;

    /**
     * 渠道配置ID
     */
    @TableField("channel_config_id")
    private Long channelConfigId;

    /**
     * 渲染后的消息内容
     */
    @TableField("rendered_content")
    private String renderedContent;

    /**
     * 任务状态：PENDING/SENDING/SUCCESS/FAILED/RETRY
     */
    @TableField("status")
    private TaskStatus status;

    /**
     * 已重试次数
     */
    @TableField("retry_count")
    private Integer retryCount;

    /**
     * 最大重试次数
     */
    @TableField("max_retry")
    private Integer maxRetry;

    /**
     * 下次重试时间
     */
    @TableField("next_retry_time")
    private LocalDateTime nextRetryTime;

    /**
     * 发送结果消息
     */
    @TableField("result_message")
    private String resultMessage;

    /**
     * 第三方响应
     */
    @TableField("third_party_response")
    private String thirdPartyResponse;

    /**
     * 创建时间（分区键）
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 完成时间
     */
    @TableField("finish_time")
    private LocalDateTime finishTime;
}