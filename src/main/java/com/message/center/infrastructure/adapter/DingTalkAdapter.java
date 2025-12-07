package com.message.center.infrastructure.adapter;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.message.center.domain.entity.ChannelConfig;
import com.message.center.domain.entity.ChannelTask;
import com.message.center.domain.enums.ChannelType;
import com.message.center.domain.vo.SendResult;
import com.message.center.repository.ChannelConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 钉钉适配器
 * 处理钉钉消息的发送逻辑
 */
@Component
public class DingTalkAdapter implements ChannelAdapter {

    private static final Logger log = LoggerFactory.getLogger(DingTalkAdapter.class);

    @Autowired
    private ChannelConfigRepository channelConfigRepository;

    @Override
    public ChannelType getChannelType() {
        return ChannelType.DINGTALK;
    }

    @Override
    public SendResult send(ChannelTask task) {
        try {
            // 获取渠道配置
            ChannelConfig config = channelConfigRepository.getById(task.getChannelConfigId());
            if (config == null) {
                return SendResult.fail("DINGTALK_CONFIG_ERROR", "钉钉渠道配置不存在");
            }

            // 解析渠道配置
            JSONObject configJson = JSON.parseObject(config.getConfigJson());
            String webhook = configJson.getString("webhook");
            String secret = configJson.getString("secret");

            if (webhook == null || webhook.isEmpty()) {
                return SendResult.fail("DINGTALK_CONFIG_ERROR", "钉钉webhook地址不能为空");
            }

            // 构建消息内容
            Map<String, Object> message = new HashMap<>();
            message.put("msgtype", "text");
            Map<String, String> text = new HashMap<>();
            text.put("content", task.getRenderedContent());
            message.put("text", text);

            // 发送消息
            String response = HttpUtil.post(webhook, JSON.toJSONString(message));
            log.debug("钉钉消息发送响应：{}", response);

            // 解析响应
            JSONObject result = JSON.parseObject(response);
            if (result.getInteger("errcode") == 0) {
                return SendResult.success(task.getMessageId());
            } else {
                return SendResult.fail("DINGTALK_SEND_ERROR", 
                        "钉钉消息发送失败：" + result.getString("errmsg"));
            }
        } catch (Exception e) {
            log.error("钉钉消息发送失败，消息ID：{}", task.getMessageId(), e);
            return SendResult.fail("DINGTALK_SEND_ERROR", "钉钉消息发送失败：" + e.getMessage());
        }
    }
}
