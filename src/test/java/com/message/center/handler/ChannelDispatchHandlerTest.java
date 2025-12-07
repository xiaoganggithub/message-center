package com.message.center.handler;

import com.message.center.domain.entity.ChannelConfig;
import com.message.center.domain.enums.ChannelType;
import com.message.center.domain.vo.MessageContext;
import com.message.center.domain.vo.HandlerResult;
import com.message.center.executor.ChannelTaskExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 渠道分发处理器单元测试
 */
@ExtendWith(MockitoExtension.class)
class ChannelDispatchHandlerTest {

    @Mock
    private ChannelTaskExecutor channelTaskExecutor;

    @InjectMocks
    private ChannelDispatchHandler channelDispatchHandler;

    private MessageContext mockContext;
    private List<ChannelConfig> mockChannelConfigs;

    @BeforeEach
    void setUp() {
        // 构建模拟上下文
        mockContext = new MessageContext();
        mockContext.setMessageId("MSG202312060001");
        mockContext.setTenantId(1001L);
        mockContext.setBusinessType("ORDER_NOTIFY");
        mockContext.setBusinessData("{\"orderId\":\"ORD123456\"}");
        mockContext.setRenderedMessages(Map.of(
                ChannelType.LOCAL, "本地消息内容",
                ChannelType.DINGTALK, "钉钉消息内容"
        ));

        // 构建模拟渠道配置
        mockChannelConfigs = new ArrayList<>();
        ChannelConfig localConfig = new ChannelConfig();
        localConfig.setId(1L);
        localConfig.setChannelType(ChannelType.LOCAL);
        localConfig.setChannelName("本地消息");
        localConfig.setEnabled(true);
        localConfig.setPriority(1);
        mockChannelConfigs.add(localConfig);

        ChannelConfig dingTalkConfig = new ChannelConfig();
        dingTalkConfig.setId(2L);
        dingTalkConfig.setChannelType(ChannelType.DINGTALK);
        dingTalkConfig.setChannelName("钉钉机器人");
        dingTalkConfig.setEnabled(true);
        dingTalkConfig.setPriority(1);
        mockChannelConfigs.add(dingTalkConfig);

        mockContext.setChannelConfigs(mockChannelConfigs);
    }

    /**
     * 测试渠道分发成功的情况
     */
    @Test
    void testHandle_Success() {
        // 执行测试
        HandlerResult result = channelDispatchHandler.handle(mockContext);

        // 验证结果
        assertTrue(result.isSuccess());
        assertTrue(result.isContinueChain());
        assertNull(result.getErrorCode());
        assertNull(result.getErrorMessage());

        // 验证任务执行器被调用
        verify(channelTaskExecutor, times(1)).executeAll(anyList());

        // 验证上下文已设置渠道任务
        assertNotNull(mockContext.getChannelTasks());
        assertEquals(2, mockContext.getChannelTasks().size());
    }

    /**
     * 测试渠道配置为空的情况
     */
    @Test
    void testHandle_Fail_ChannelConfigsEmpty() {
        // 清空渠道配置
        mockContext.setChannelConfigs(new ArrayList<>());

        // 执行测试
        HandlerResult result = channelDispatchHandler.handle(mockContext);

        // 验证结果
        assertTrue(result.isSuccess());
        assertTrue(result.isContinueChain());

        // 验证任务执行器未被调用
        verify(channelTaskExecutor, never()).executeAll(anyList());
    }

    /**
     * 测试获取处理器顺序
     */
    @Test
    void testGetOrder() {
        assertEquals(600, channelDispatchHandler.getOrder());
    }

    /**
     * 测试获取处理器名称
     */
    @Test
    void testGetName() {
        assertEquals("渠道分发处理器", channelDispatchHandler.getName());
    }

    /**
     * 测试支持处理的情况
     */
    @Test
    void testSupports() {
        // 有渠道配置的情况
        assertTrue(channelDispatchHandler.supports(mockContext));

        // 无渠道配置的情况
        mockContext.setChannelConfigs(new ArrayList<>());
        assertFalse(channelDispatchHandler.supports(mockContext));
    }
}
