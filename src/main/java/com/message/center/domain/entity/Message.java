package com.message.center.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.message.center.domain.enums.MessageStatus;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息主表
 * 对应数据库表：msg_message
 */
@Data
@TableName("msg_message")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 消息唯一ID
     */
    @TableField(value = "message_id", unique = true, insertStrategy = FieldStrategy.NOT_EMPTY)
    private String messageId;

    /**
     * 租户ID
     */
    @TableField("tenant_id")
    private Long tenantId;

    /**
     * 门店ID（空表示租户级）
     */
    @TableField("store_id")
    private Long storeId;

    /**
     * 业务类型
     */
    @TableField("business_type")
    private String businessType;

    /**
     * 业务数据JSON
     */
    @TableField("business_data")
    private String businessData;

    /**
     * 目标渠道列表
     */
    @TableField("target_channels")
    private String targetChannels;

    /**
     * 消息状态：PENDING/PROCESSING/SUCCESS/PARTIAL_SUCCESS/FAILED
     */
    @TableField("status")
    private MessageStatus status;

    /**
     * 总渠道数
     */
    @TableField("total_channels")
    private Integer totalChannels;

    /**
     * 成功渠道数
     */
    @TableField("success_channels")
    private Integer successChannels;

    /**
     * 失败渠道数
     */
    @TableField("failed_channels")
    private Integer failedChannels;

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