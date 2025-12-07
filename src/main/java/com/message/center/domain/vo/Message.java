package com.message.center.domain.vo;

import com.message.center.domain.enums.ChannelType;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 消息对象
 * 用于Dubbo和REST接口的参数传输
 */
@Data
public class Message {
    /**
     * 消息ID（可选，系统自动生成）
     */
    private String messageId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 门店ID（可选）
     */
    private Long storeId;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 业务数据（JSON格式）
     */
    private String businessData;

    /**
     * 目标渠道列表（可选，不指定则使用配置的渠道）
     */
    private List<ChannelType> targetChannels;

    /**
     * 扩展属性
     */
    private Map<String, Object> attributes;
}
