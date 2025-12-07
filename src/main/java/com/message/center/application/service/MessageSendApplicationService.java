package com.message.center.application.service;

import com.message.center.domain.vo.SendResult;
import com.message.center.domain.vo.Message;

import java.util.List;

/**
 * 消息发送应用服务接口
 */
public interface MessageSendApplicationService {
    /**
     * 发送单条消息
     * @param message 消息对象
     * @return 发送结果
     */
    SendResult sendMessage(Message message);

    /**
     * 批量发送消息
     * @param messages 消息列表
     * @return 批量发送结果
     */
    BatchSendResult batchSendMessages(List<Message> messages);

    /**
     * 批量发送结果
     */
    class BatchSendResult {
        /** 总消息数 */
        private int totalCount;
        /** 成功数 */
        private int successCount;
        /** 失败数 */
        private int failedCount;
        /** 详细结果列表 */
        private List<SendResult> results;

        // 构造方法
        public BatchSendResult(int totalCount, int successCount, int failedCount, List<SendResult> results) {
            this.totalCount = totalCount;
            this.successCount = successCount;
            this.failedCount = failedCount;
            this.results = results;
        }

        // getter和setter方法
        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public int getSuccessCount() {
            return successCount;
        }

        public void setSuccessCount(int successCount) {
            this.successCount = successCount;
        }

        public int getFailedCount() {
            return failedCount;
        }

        public void setFailedCount(int failedCount) {
            this.failedCount = failedCount;
        }

        public List<SendResult> getResults() {
            return results;
        }

        public void setResults(List<SendResult> results) {
            this.results = results;
        }
    }
}