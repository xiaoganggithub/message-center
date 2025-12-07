package com.message.center.domain.vo;

import com.message.center.domain.enums.ChannelType;
import com.message.center.domain.entity.ChannelConfig;
import com.message.center.domain.entity.ChannelTask;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息处理上下文
 * 在责任链中传递，包含消息处理所需的所有数据
 */
@Data
@NoArgsConstructor
public class MessageContext {
    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 原始业务数据(JSON)
     */
    private String businessData;

    /**
     * 目标渠道列表
     */
    private List<ChannelType> targetChannels;

    /**
     * 渠道配置列表
     */
    private List<ChannelConfig> channelConfigs;

    /**
     * 渲染后的消息内容(按渠道)
     */
    private Map<ChannelType, String> renderedMessages;

    /**
     * 各渠道发送任务
     */
    private List<ChannelTask> channelTasks;

    /**
     * 处理状态
     */
    private ProcessStatus status;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 扩展属性
     */
    private Map<String, Object> attributes;

    /**
     * 处理状态枚举
     */
    public enum ProcessStatus {
        INIT,        // 初始状态
        PROCESSING,  // 处理中
        SUCCESS,     // 处理成功
        FAILED       // 处理失败
    }

    /**
     * 添加扩展属性
     * @param key 键
     * @param value 值
     */
    public void addAttribute(String key, Object value) {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        attributes.put(key, value);
    }

    /**
     * 获取扩展属性
     * @param key 键
     * @param <T> 类型
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        if (attributes == null) {
            return null;
        }
        return (T) attributes.get(key);
    }

    /**
     * 添加渲染后的消息
     * @param channelType 渠道类型
     * @param content 渲染后的内容
     */
    public void addRenderedMessage(ChannelType channelType, String content) {
        if (renderedMessages == null) {
            renderedMessages = new HashMap<>();
        }
        renderedMessages.put(channelType, content);
    }

    /**
     * 添加渠道任务
     * @param task 渠道任务
     */
    public void addChannelTask(ChannelTask task) {
        if (channelTasks == null) {
            channelTasks = new ArrayList<>();
        }
        channelTasks.add(task);
    }
}