package com.message.center.application.handler;

import com.message.center.domain.vo.MessageContext;
import com.message.center.domain.vo.HandlerResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;

/**
 * 责任链执行器
 * 负责按顺序执行所有处理器
 */
@Component
public class MessageHandlerChain {

    @Autowired
    private List<MessageHandler> handlers;

    @PostConstruct
    public void init() {
        // 按order排序
        handlers.sort(Comparator.comparingInt(MessageHandler::getOrder));
    }

    /**
     * 执行责任链
     * @param context 消息处理上下文
     * @return 责任链执行结果
     */
    public ChainResult execute(MessageContext context) {
        ChainResult result = new ChainResult();
        result.setStartTime(System.currentTimeMillis());

        // 遍历所有处理器
        for (MessageHandler handler : handlers) {
            // 检查是否支持处理
            if (!handler.supports(context)) {
                continue;
            }

            try {
                // 执行处理器
                HandlerResult handlerResult = handler.handle(context);
                result.addHandlerResult(handler.getName(), handlerResult);

                // 如果处理失败或不继续执行，终止责任链
                if (!handlerResult.isContinueChain()) {
                    result.setSuccess(handlerResult.isSuccess());
                    result.setErrorMessage(handlerResult.getErrorMessage());
                    break;
                }
            } catch (Exception e) {
                result.setSuccess(false);
                result.setErrorMessage("处理器[" + handler.getName() + "]执行异常: " + e.getMessage());
                break;
            }
        }

        result.setEndTime(System.currentTimeMillis());
        return result;
    }

    /**
     * 责任链执行结果
     */
    public static class ChainResult {
        /** 开始时间 */
        private long startTime;
        /** 结束时间 */
        private long endTime;
        /** 是否成功 */
        private boolean success;
        /** 错误信息 */
        private String errorMessage;
        /** 处理器执行结果列表 */
        private java.util.Map<String, HandlerResult> handlerResults;

        // 构造方法
        public ChainResult() {
            this.handlerResults = new java.util.HashMap<>();
            this.success = true;
        }

        // 添加处理器执行结果
        public void addHandlerResult(String handlerName, HandlerResult result) {
            this.handlerResults.put(handlerName, result);
        }

        // getter和setter方法
        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public java.util.Map<String, HandlerResult> getHandlerResults() {
            return handlerResults;
        }

        public void setHandlerResults(java.util.Map<String, HandlerResult> handlerResults) {
            this.handlerResults = handlerResults;
        }
    }
}
