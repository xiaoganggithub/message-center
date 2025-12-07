package com.message.center.application.handler;

import com.message.center.domain.entity.ChannelConfig;
import com.message.center.domain.vo.MessageContext;
import com.message.center.domain.vo.HandlerResult;
import com.message.center.repository.ChannelConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 时间窗口处理器
 * 顺序：200
 * 职责：检查发送时间窗口
 */
@Component
public class TimeWindowHandler implements MessageHandler {

    @Autowired
    private ChannelConfigRepository channelConfigRepository;

    @Override
    public HandlerResult handle(MessageContext context) {
        // 获取当前时间的小时数
        int currentHour = LocalDateTime.now().getHour();

        // 获取渠道配置
        List<ChannelConfig> channelConfigs = channelConfigRepository.getEnabledConfigs(
                context.getTenantId(),
                context.getStoreId()
        );

        if (channelConfigs.isEmpty()) {
            return HandlerResult.fail("NO_CHANNEL", "未配置可用的发送渠道");
        }

        // 检查每个渠道的时间窗口
        for (ChannelConfig config : channelConfigs) {
            if (config.getEnabled() && config.getTimeWindowEnabled()) {
                int startHour = config.getTimeWindowStartHour() != null ? config.getTimeWindowStartHour() : 0;
                int endHour = config.getTimeWindowEndHour() != null ? config.getTimeWindowEndHour() : 23;

                // 检查当前时间是否在允许的时间窗口内
                if (currentHour < startHour || currentHour > endHour) {
                    return HandlerResult.fail("NOT_IN_TIME_WINDOW", 
                            String.format("当前时间不在允许发送的时间窗口内，允许时间：%02d:00-%02d:00", startHour, endHour));
                }
            }
        }

        return HandlerResult.success();
    }

    @Override
    public int getOrder() {
        return 200;
    }

    @Override
    public String getName() {
        return "时间窗口处理器";
    }

    @Override
    public boolean supports(MessageContext context) {
        return true;
    }
}
