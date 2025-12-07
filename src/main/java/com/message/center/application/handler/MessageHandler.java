package com.message.center.application.handler;

import com.message.center.domain.vo.MessageContext;
import com.message.center.domain.vo.HandlerResult;

/**
 * 消息处理器接口
 * 责任链模式的核心接口，所有处理器必须实现此接口
 */
public interface MessageHandler {
    /**
     * 处理消息
     * @param context 消息处理上下文，包含消息数据和处理状态
     * @return 处理结果
     */
    HandlerResult handle(MessageContext context);

    /**
     * 获取处理器顺序，数值越小优先级越高
     * @return 顺序值
     */
    int getOrder();

    /**
     * 获取处理器名称
     * @return 处理器名称
     */
    String getName();

    /**
     * 是否支持处理当前消息
     * @param context 消息处理上下文
     * @return true表示支持处理
     */
    boolean supports(MessageContext context);
}
