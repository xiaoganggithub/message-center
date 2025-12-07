package com.message.center.application.handler;

import com.message.center.domain.enums.ChannelType;
import com.message.center.domain.entity.MessageTemplate;
import com.message.center.domain.vo.MessageContext;
import com.message.center.domain.vo.HandlerResult;
import com.message.center.interfaces.dubbo.api.MessageTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 模板渲染处理器
 * 顺序：500
 * 职责：根据业务数据和模板生成最终消息内容
 */
@Component
public class TemplateRenderHandler implements MessageHandler {

    @Autowired
    private MessageTemplateService messageTemplateService;

    @Override
    public HandlerResult handle(MessageContext context) {
        Map<ChannelType, String> renderedMessages = new HashMap<>();

        // 对每个目标渠道进行模板渲染
        for (ChannelType channelType : context.getTargetChannels()) {
            // 获取匹配的消息模板
            MessageTemplate template = messageTemplateService.getTemplate(
                    context.getTenantId(),
                    context.getBusinessType(),
                    channelType
            );

            // 渲染模板
            String renderedContent = messageTemplateService.renderTemplate(
                    template,
                    context.getBusinessData()
            );

            // 保存渲染结果
            renderedMessages.put(channelType, renderedContent);
        }

        // 将渲染结果设置到上下文
        context.setRenderedMessages(renderedMessages);

        return HandlerResult.success();
    }

    @Override
    public int getOrder() {
        return 500;
    }

    @Override
    public String getName() {
        return "模板渲染处理器";
    }

    @Override
    public boolean supports(MessageContext context) {
        return context.getTargetChannels() != null && !context.getTargetChannels().isEmpty();
    }
}
