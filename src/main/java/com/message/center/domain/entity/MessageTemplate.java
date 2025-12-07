package com.message.center.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.message.center.domain.enums.ChannelType;
import com.message.center.domain.enums.MessageType;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息模板表
 * 对应数据库表：msg_template
 */
@Data
@TableName("msg_template")
public class MessageTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模板ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 租户ID
     */
    @TableField("tenant_id")
    private Long tenantId;

    /**
     * 业务类型
     */
    @TableField("business_type")
    private String businessType;

    /**
     * 渠道类型：DINGTALK/WECHAT_WORK/LOCAL
     */
    @TableField("channel_type")
    private ChannelType channelType;

    /**
     * 消息类型：TEXT/MARKDOWN/CARD/LINK
     */
    @TableField("message_type")
    private MessageType messageType;

    /**
     * 模板名称
     */
    @TableField("template_name")
    private String templateName;

    /**
     * 模板内容（使用${variableName}占位符）
     */
    @TableField("template_content")
    private String templateContent;

    /**
     * 是否启用
     */
    @TableField("enabled")
    private Boolean enabled;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}