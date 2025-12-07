package com.message.center.application.handler;

import cn.hutool.core.collection.CollUtil;
import com.message.center.domain.entity.ChannelConfig;
import com.message.center.domain.vo.MessageContext;
import com.message.center.domain.vo.HandlerResult;
import com.message.center.repository.ChannelConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 渠道路由处理器
 * 顺序：300
 * 职责：根据租户/门店配置路由到目标渠道
 */
@Component
public class ChannelRouterHandler implements MessageHandler {

    @Autowired
    private ChannelConfigRepository channelConfigRepository;

    @Override
    public HandlerResult handle(MessageContext context) {
        // 1. 获取租户/门店的渠道配置
        List<ChannelConfig> configs = channelConfigRepository.getConfigs(
                context.getTenantId(),
                context.getStoreId(),
                context.getBusinessType()
        );

        if (CollUtil.isEmpty(configs)) {
            return HandlerResult.fail("NO_CHANNEL", "未配置可用的发送渠道");
        }

        // 2. 过滤启用的渠道
        List<ChannelConfig> enabledConfigs = configs.stream()
                .filter(ChannelConfig::getEnabled)
                .collect(Collectors.toList());

        if (CollUtil.isEmpty(enabledConfigs)) {
            return HandlerResult.fail("NO_CHANNEL", "未配置可用的发送渠道");
        }

        // 3. 如果指定了目标渠道，进行过滤
        if (CollUtil.isNotEmpty(context.getTargetChannels())) {
            enabledConfigs = enabledConfigs.stream()
                    .filter(c -> context.getTargetChannels().contains(c.getChannelType()))
                    .collect(Collectors.toList());
        }

        if (CollUtil.isEmpty(enabledConfigs)) {
            return HandlerResult.fail("NO_CHANNEL", "未找到匹配的发送渠道");
        }

        // 4. 将渠道配置设置到上下文
        context.setChannelConfigs(enabledConfigs);

        return HandlerResult.success();
    }

    @Override
    public int getOrder() {
        return 300;
    }

    @Override
    public String getName() {
        return "渠道路由处理器";
    }

    @Override
    public boolean supports(MessageContext context) {
        return true;
    }
}
