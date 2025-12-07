package com.message.center.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.message.center.domain.enums.ChannelType;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 渠道配置表
 * 对应数据库表：msg_channel_config
 */
@Data
@TableName("msg_channel_config")
public class ChannelConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 配置ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

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
     * 渠道类型：DINGTALK/WECHAT_WORK/LOCAL
     */
    @TableField("channel_type")
    private ChannelType channelType;

    /**
     * 渠道名称
     */
    @TableField("channel_name")
    private String channelName;

    /**
     * 渠道配置JSON（认证信息、webhook等）
     */
    @TableField("config_json")
    private String configJson;

    /**
     * 优先级（数值越小优先级越高）
     */
    @TableField("priority")
    private Integer priority;

    /**
     * 是否启用
     */
    @TableField("enabled")
    private Boolean enabled;

    /**
     * 频次限制数量
     */
    @TableField("rate_limit_count")
    private Integer rateLimitCount;

    /**
     * 频次限制时间窗口（秒）
     */
    @TableField("rate_limit_window")
    private Integer rateLimitWindow;

    /**
     * 时间单位：SECOND/MINUTE/HOUR/DAY
     */
    @TableField("rate_limit_unit")
    private String rateLimitUnit;

    /**
     * 是否启用时间窗口限制
     */
    @TableField("time_window_enabled")
    private Boolean timeWindowEnabled;

    /**
     * 允许发送开始时间（小时，0-23）
     */
    @TableField("time_window_start_hour")
    private Integer timeWindowStartHour;

    /**
     * 允许发送结束时间（小时，0-23）
     */
    @TableField("time_window_end_hour")
    private Integer timeWindowEndHour;

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