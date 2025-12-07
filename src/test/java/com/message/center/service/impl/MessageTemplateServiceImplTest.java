package com.message.center.service.impl;

import com.message.center.domain.entity.MessageTemplate;
import com.message.center.domain.enums.ChannelType;
import com.message.center.repository.MessageTemplateRepository;
import com.message.center.service.api.MessageTemplateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 消息模板服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class MessageTemplateServiceImplTest {

    @Mock
    private MessageTemplateRepository messageTemplateRepository;

    @InjectMocks
    private MessageTemplateServiceImpl messageTemplateService;

    private MessageTemplate mockTemplate;

    @BeforeEach
    void setUp() {
        // 创建模拟模板
        mockTemplate = new MessageTemplate();
        mockTemplate.setId(1L);
        mockTemplate.setTenantId(1001L);
        mockTemplate.setBusinessType("ORDER_NOTIFY");
        mockTemplate.setChannelType(ChannelType.LOCAL);
        mockTemplate.setTemplateContent("您有一笔新订单：${orderId}，客户：${customerName}，金额：${amount}元");
        mockTemplate.setEnabled(true);
    }

    /**
     * 测试渲染消息模板
     */
    @Test
    void testRenderTemplate() {
        // 准备测试数据
        String businessData = "{\"orderId\":\"ORD123456\",\"customerName\":\"张三\",\"amount\":199.0}";
        String expectedContent = "您有一笔新订单：ORD123456，客户：张三，金额：199.0元";

        // 执行测试
        String result = messageTemplateService.renderTemplate(mockTemplate, businessData);

        // 验证结果
        assertEquals(expectedContent, result);
    }

    /**
     * 测试渲染模板时模板为空的情况
     */
    @Test
    void testRenderTemplate_TemplateNull() {
        // 准备测试数据
        String businessData = "{\"orderId\":\"ORD123456\"}";

        // 执行测试
        String result = messageTemplateService.renderTemplate(null, businessData);

        // 验证结果
        assertEquals(businessData, result);
    }

    /**
     * 测试渲染模板时模板内容为空的情况
     */
    @Test
    void testRenderTemplate_TemplateContentEmpty() {
        // 准备测试数据
        mockTemplate.setTemplateContent(null);
        String businessData = "{\"orderId\":\"ORD123456\"}";

        // 执行测试
        String result = messageTemplateService.renderTemplate(mockTemplate, businessData);

        // 验证结果
        assertEquals(businessData, result);
    }

    /**
     * 测试渲染模板时业务数据格式错误的情况
     */
    @Test
    void testRenderTemplate_InvalidBusinessData() {
        // 准备测试数据
        String businessData = "invalid json";

        // 执行测试
        String result = messageTemplateService.renderTemplate(mockTemplate, businessData);

        // 验证结果
        assertEquals(businessData, result);
    }

    /**
     * 测试获取消息模板
     */
    @Test
    void testGetTemplate() {
        // 模拟仓库返回
        when(messageTemplateRepository.getTemplate(1001L, "ORDER_NOTIFY", ChannelType.LOCAL))
                .thenReturn(mockTemplate);

        // 执行测试
        MessageTemplate result = messageTemplateService.getTemplate(1001L, "ORDER_NOTIFY", ChannelType.LOCAL);

        // 验证结果
        assertNotNull(result);
        assertEquals(mockTemplate.getId(), result.getId());
        verify(messageTemplateRepository, times(1))
                .getTemplate(1001L, "ORDER_NOTIFY", ChannelType.LOCAL);
    }

    /**
     * 测试保存消息模板
     */
    @Test
    void testSave() {
        // 模拟仓库返回
        when(messageTemplateRepository.save(mockTemplate)).thenReturn(true);

        // 执行测试
        boolean result = messageTemplateService.save(mockTemplate);

        // 验证结果
        assertTrue(result);
        verify(messageTemplateRepository, times(1)).save(mockTemplate);
    }

    /**
     * 测试更新消息模板
     */
    @Test
    void testUpdate() {
        // 模拟仓库返回
        when(messageTemplateRepository.update(mockTemplate)).thenReturn(true);

        // 执行测试
        boolean result = messageTemplateService.update(mockTemplate);

        // 验证结果
        assertTrue(result);
        verify(messageTemplateRepository, times(1)).update(mockTemplate);
    }

    /**
     * 测试删除消息模板
     */
    @Test
    void testDelete() {
        // 模拟仓库返回
        when(messageTemplateRepository.delete(1L)).thenReturn(true);

        // 执行测试
        boolean result = messageTemplateService.delete(1L);

        // 验证结果
        assertTrue(result);
        verify(messageTemplateRepository, times(1)).delete(1L);
    }
}
