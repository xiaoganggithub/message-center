package com.message.center.infrastructure.adapter;

import com.message.center.domain.entity.ChannelTask;
import com.message.center.domain.enums.ChannelType;
import com.message.center.domain.vo.SendResult;

/**
 * 渠道适配器接口
 * 定义了所有渠道适配器必须实现的方法
 */
public interface ChannelAdapter {
    /**
     * 获取支持的渠道类型
     * @return 渠道类型
     */
    ChannelType getChannelType();

    /**
     * 发送消息
     * @param task 渠道任务
     * @return 发送结果
     */
    SendResult send(ChannelTask task);
}
