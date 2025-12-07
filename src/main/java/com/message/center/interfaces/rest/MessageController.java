package com.message.center.interfaces.rest;

import com.message.center.domain.vo.Message;
import com.message.center.domain.vo.SendResult;
import com.message.center.application.service.MessageSendApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息控制器
 * 处理消息发送和查询相关的REST接口
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageSendApplicationService messageSendApplicationService;

    /**
     * 发送单条消息
     * @param message 消息对象
     * @return 发送结果
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SendResult>> sendMessage(@RequestBody Message message) {
        try {
            SendResult result = messageSendApplicationService.sendMessage(message);
            if (result.isSuccess()) {
                return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "success", result));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.getErrorMessage(), result));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "发送消息失败: " + e.getMessage(), null));
        }
    }

    /**
     * 批量发送消息
     * @param messages 消息列表
     * @return 批量发送结果
     */
    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<MessageSendApplicationService.BatchSendResult>> batchSendMessages(@RequestBody List<Message> messages) {
        try {
            MessageSendApplicationService.BatchSendResult result = messageSendApplicationService.batchSendMessages(messages);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "success", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "批量发送消息失败: " + e.getMessage(), null));
        }
    }

    /**
     * API响应通用格式
     * @param <T> 响应数据类型
     */
    public static class ApiResponse<T> {
        /** 响应码 */
        private int code;
        /** 响应消息 */
        private String message;
        /** 响应数据 */
        private T data;

        // 构造方法
        public ApiResponse(int code, String message, T data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }

        // getter和setter方法
        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }
}
