package com.message.center.domain.enums;

import lombok.Getter;

/**
 * 消息状态枚举
 */
@Getter
public enum MessageStatus {
    PENDING("待处理", "消息已接收，等待处理"),
    PROCESSING("处理中", "消息正在处理中"),
    SUCCESS("全部成功", "所有渠道消息发送成功"),
    PARTIAL_SUCCESS("部分成功", "部分渠道消息发送成功"),
    FAILED("全部失败", "所有渠道消息发送失败");

    private final String name;
    private final String description;

    MessageStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }
}