package com.message.center.domain.vo;

import lombok.Data;

/**
 * 发送结果
 */
@Data
public class SendResult {
    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 成功结果
     * @param messageId 消息ID
     * @return 成功结果
     */
    public static SendResult success(String messageId) {
        SendResult result = new SendResult();
        result.setSuccess(true);
        result.setMessageId(messageId);
        return result;
    }

    /**
     * 失败结果
     * @param errorCode 错误码
     * @param errorMessage 错误信息
     * @return 失败结果
     */
    public static SendResult fail(String errorCode, String errorMessage) {
        SendResult result = new SendResult();
        result.setSuccess(false);
        result.setErrorCode(errorCode);
        result.setErrorMessage(errorMessage);
        return result;
    }
}
