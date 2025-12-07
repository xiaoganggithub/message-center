package com.message.center.domain.enums;

import lombok.Getter;

/**
 * 消息类型枚举
 */
@Getter
public enum MessageType {
    TEXT("文本", "文本消息"),
    MARKDOWN("Markdown", "Markdown消息"),
    CARD("卡片", "卡片消息"),
    LINK("链接", "链接消息");

    private final String name;
    private final String description;

    MessageType(String name, String description) {
        this.name = name;
        this.description = description;
    }
}