package com.message.center.handler;

import com.message.center.domain.enums.ChannelType;
import com.message.center.domain.enums.TimeUnitEnum;
import com.message.center.domain.vo.MessageContext;
import com.message.center.domain.vo.HandlerResult;
import com.message.center.service.api.ChannelConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 频次控制处理器单元测试
 */
@ExtendWith(MockitoExtension.class)
class RateLimitHandlerTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ChannelConfigService channelConfigService;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RateLimitHandler rateLimitHandler;

    private MessageContext mockContext;

    @BeforeEach
    void setUp() {
        // 构建模拟上下文
        mockContext = new MessageContext();
        mockContext.setMessageId("MSG202312060001");
        mockContext.setTenantId(1001L);
        mockContext.setStoreId(2001L);
        mockContext.setBusinessType("ORDER_NOTIFY");
        mockContext.setTargetChannels(List.of(ChannelType.LOCAL, ChannelType.DINGTALK));

        // 模拟Redis操作
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(1L);

        // 模拟渠道配置服务返回频次限制配置
        ChannelConfigService.RateLimitConfig rateLimitConfig = new ChannelConfigService.RateLimitConfig(100, 60, TimeUnitEnum.SECOND.getCode());
        when(channelConfigService.getRateLimitConfig(anyLong(), anyLong(), any(ChannelType.class))).thenReturn(rateLimitConfig);
    }

    /**
     * 测试频次控制通过的情况
     */
    @Test
    void testHandle_Success() {
        // 执行测试
        HandlerResult result = rateLimitHandler.handle(mockContext);

        // 验证结果
        assertTrue(result.isSuccess());
        assertTrue(result.isContinueChain());
        assertNull(result.getErrorCode());
        assertNull(result.getErrorMessage());

        // 验证Redis操作
        verify(redisTemplate.opsForValue(), times(2)).increment(anyString());
    }

    /**
     * 测试频次控制失败的情况
     */
    @Test
    void testHandle_Fail_RateLimited() {
        // 模拟Redis返回超过限制的计数
        when(valueOperations.increment(anyString())).thenReturn(101L);

        // 执行测试
        HandlerResult result = rateLimitHandler.handle(mockContext);

        // 验证结果
        assertFalse(result.isSuccess());
        assertFalse(result.isContinueChain());
        assertEquals("RATE_LIMITED", result.getErrorCode());
        assertNotNull(result.getErrorMessage());
    }

    /**
     * 测试获取处理器顺序
     */
    @Test
    void testGetOrder() {
        assertEquals(400, rateLimitHandler.getOrder());
    }

    /**
     * 测试获取处理器名称
     */
    @Test
    void testGetName() {
        assertEquals("频次控制处理器", rateLimitHandler.getName());
    }

    /**
     * 测试支持处理的情况
     */
    @Test
    void testSupports() {
        // 有渠道配置的情况
        assertTrue(rateLimitHandler.supports(mockContext));
    }
}
