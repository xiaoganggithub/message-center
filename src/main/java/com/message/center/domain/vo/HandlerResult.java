package com.message.center.domain.vo;

import lombok.Data;

/**
 * 处理器执行结果
 */
@Data
public class HandlerResult {
    /**
     * 是否继续执行下一个处理器
     */
    private boolean continueChain;

    /**
     * 是否处理成功
     */
    private boolean success;

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 构造方法
     */
    public HandlerResult(boolean continueChain, boolean success, String errorCode, String errorMessage) {
        this.continueChain = continueChain;
        this.success = success;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * 成功结果
     * @return 成功结果
     */
    public static HandlerResult success() {
        return new HandlerResult(true, true, null, null);
    }

    /**
     * 失败结果
     * @param errorCode 错误码
     * @param errorMessage 错误信息
     * @return 失败结果
     */
    public static HandlerResult fail(String errorCode, String errorMessage) {
        return new HandlerResult(false, false, errorCode, errorMessage);
    }

    /**
     * 跳过结果
     * @return 跳过结果
     */
    public static HandlerResult skip() {
        return new HandlerResult(true, true, null, "跳过当前处理器");
    }
}
