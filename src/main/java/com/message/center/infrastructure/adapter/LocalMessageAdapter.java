package com.message.center.infrastructure.adapter;

import com.message.center.domain.entity.ChannelTask;
import com.message.center.domain.enums.ChannelType;
import com.message.center.domain.vo.SendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 本地消息适配器
 * 处理本地消息的发送逻辑
 */
@Component
public class LocalMessageAdapter implements ChannelAdapter {

    private static final Logger log = LoggerFactory.getLogger(LocalMessageAdapter.class);

    @Override
    public ChannelType getChannelType() {
        return ChannelType.LOCAL;
    }

    @Override
    public SendResult send(ChannelTask task) {
        try {
            // 本地消息发送逻辑，这里简化处理，只记录日志
            log.info("本地消息发送成功，消息ID：{}, 内容：{}", task.getMessageId(), task.getRenderedContent());
            return SendResult.success(task.getMessageId());
        } catch (Exception e) {
            log.error("本地消息发送失败，消息ID：{}", task.getMessageId(), e);
            return SendResult.fail("LOCAL_CHANNEL_ERROR", "本地消息发送失败：" + e.getMessage());
        }
    }
}
