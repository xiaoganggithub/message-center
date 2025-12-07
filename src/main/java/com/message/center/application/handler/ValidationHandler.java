package com.message.center.application.handler;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.message.center.domain.vo.MessageContext;
import com.message.center.domain.vo.HandlerResult;
import org.springframework.stereotype.Component;

/**
 * 消息验证处理器
 * 顺序：100
 * 职责：验证消息的必填字段、格式等
 */
@Component
public class ValidationHandler implements MessageHandler {

    @Override
    public HandlerResult handle(MessageContext context) {
        // 1. 验证必填字段
        if (context.getTenantId() == null) {
            return HandlerResult.fail("VALIDATION_ERROR", "租户ID不能为空");
        }

        if (StrUtil.isBlank(context.getBusinessType())) {
            return HandlerResult.fail("VALIDATION_ERROR", "业务类型不能为空");
        }

        if (StrUtil.isBlank(context.getBusinessData())) {
            return HandlerResult.fail("VALIDATION_ERROR", "业务数据不能为空");
        }

        // 2. 验证JSON格式
        try {
            JSON.parse(context.getBusinessData());
        } catch (Exception e) {
            return HandlerResult.fail("VALIDATION_ERROR", "业务数据必须是有效的JSON格式");
        }

        return HandlerResult.success();
    }

    @Override
    public int getOrder() {
        return 100;
    }

    @Override
    public String getName() {
        return "消息验证处理器";
    }

    @Override
    public boolean supports(MessageContext context) {
        return true;
    }
}
