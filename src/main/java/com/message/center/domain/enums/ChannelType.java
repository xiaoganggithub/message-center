package com.message.center.domain.enums;

import lombok.Getter;

/**
 * 渠道类型枚举
 */
@Getter
public enum ChannelType {
    LOCAL("本地消息", "本地消息渠道"),
    DINGTALK("钉钉", "钉钉机器人渠道"),
    WECHAT_WORK("企业微信", "企业微信群机器人渠道");

    private final String name;
    private final String description;

    ChannelType(String name, String description) {
        this.name = name;
        this.description = description;
    }
}