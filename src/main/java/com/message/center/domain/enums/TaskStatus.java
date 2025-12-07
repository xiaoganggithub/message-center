package com.message.center.domain.enums;

import lombok.Getter;

/**
 * 任务状态枚举
 */
@Getter
public enum TaskStatus {
    PENDING("待发送", "任务已创建，等待发送"),
    SENDING("发送中", "任务正在发送中"),
    SUCCESS("发送成功", "任务发送成功"),
    FAILED("发送失败", "任务发送失败，且已达到最大重试次数"),
    RETRY("等待重试", "任务发送失败，等待重试"),
    CANCELLED("已取消", "任务已取消");

    private final String name;
    private final String description;

    TaskStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }
}