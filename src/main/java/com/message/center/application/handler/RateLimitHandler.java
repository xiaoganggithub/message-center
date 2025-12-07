package com.message.center.application.handler;

import cn.hutool.core.collection.CollUtil;
import com.message.center.domain.enums.ChannelType;
import com.message.center.domain.enums.TimeUnitEnum;
import com.message.center.domain.vo.MessageContext;
import com.message.center.domain.vo.HandlerResult;
import com.message.center.interfaces.dubbo.api.ChannelConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 频次控制处理器
 * 顺序：400
 * 职责：检查各渠道的发送频次限制
 * 设计原则：
 * - 租户隔离：每个租户的每个渠道维护独立的频次计数器，互不影响
 * - 灵活配置：支持不同的时间窗口（秒/分钟/小时/天）和限制数量
 * - 实现方式：基于 Redis 的计数器 + TTL 实现滑动时间窗口
 * - Key 设计规则：rate_limit:{channelType}:{tenantId}:{storeId}:{timeWindow}
 */
@Component
public class RateLimitHandler implements MessageHandler {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ChannelConfigService channelConfigService;

    @Override
    public HandlerResult handle(MessageContext context) {
        List<ChannelType> allowedChannels = new ArrayList<>();

        // 检查每个渠道的频次限制
        for (ChannelType channelType : context.getTargetChannels()) {
            // 获取渠道的频次限制配置
            ChannelConfigService.RateLimitConfig rateLimitConfig = channelConfigService.getRateLimitConfig(
                    context.getTenantId(),
                    context.getStoreId(),
                    channelType
            );

            // 构建 Redis Key
            String rateLimitKey = buildRateLimitKey(
                    channelType,
                    context.getTenantId(),
                    context.getStoreId(),
                    rateLimitConfig
            );

            // 检查当前时间窗口内的发送次数
            Long currentCount = redisTemplate.opsForValue().increment(rateLimitKey);

            // 首次计数时设置过期时间
            if (currentCount == 1) {
                long ttlSeconds = getTTLSeconds(rateLimitConfig);
                redisTemplate.expire(rateLimitKey, ttlSeconds, TimeUnit.SECONDS);
            }

            // 判断是否超过限制
            if (currentCount <= rateLimitConfig.getCount()) {
                allowedChannels.add(channelType);
            } else {
                return HandlerResult.fail("RATE_LIMITED", 
                        String.format("渠道%s触发频次限制，当前窗口已发送%d条，限制%d条", 
                                channelType.getName(), currentCount, rateLimitConfig.getCount()));
            }
        }

        // 更新上下文中的可用渠道列表
        context.setTargetChannels(allowedChannels);

        // 如果所有渠道都被限流，返回失败
        if (allowedChannels.isEmpty()) {
            return HandlerResult.fail("RATE_LIMITED", "所有渠道均触发频次限制，消息发送失败");
        }

        return HandlerResult.success();
    }

    @Override
    public int getOrder() {
        return 400;
    }

    @Override
    public String getName() {
        return "频次控制处理器";
    }

    @Override
    public boolean supports(MessageContext context) {
        return context.getChannelConfigs() != null && !context.getChannelConfigs().isEmpty();
    }

    /**
     * 构建频次限制的 Redis Key
     */
    private String buildRateLimitKey(ChannelType channelType, Long tenantId, Long storeId, 
                                   ChannelConfigService.RateLimitConfig config) {
        String timeWindow = calculateTimeWindow(config);
        String storeIdStr = storeId != null ? String.valueOf(storeId) : "null";
        return String.format("rate_limit:%s:%d:%s:%s",
                channelType,
                tenantId,
                storeIdStr,
                timeWindow);
    }

    /**
     * 根据时间单位计算当前时间窗口标识
     */
    private String calculateTimeWindow(ChannelConfigService.RateLimitConfig config) {
        LocalDateTime now = LocalDateTime.now();
        switch (config.getUnit()) {
            case "SECOND":
                int secondSlot = now.getSecond() / config.getWindow() * config.getWindow();
                return now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")) + "_" + secondSlot;
            case "MINUTE":
                return now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
            case "HOUR":
                return now.format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
            case "DAY":
                return now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            default:
                return now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        }
    }

    /**
     * 计算Redis Key的TTL（秒）
     */
    private long getTTLSeconds(ChannelConfigService.RateLimitConfig config) {
        TimeUnitEnum unit = TimeUnitEnum.getByCode(config.getUnit());
        return unit.toSeconds(config.getWindow());
    }
}
