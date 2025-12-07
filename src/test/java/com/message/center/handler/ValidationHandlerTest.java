package com.message.center.handler;

import com.message.center.domain.vo.MessageContext;
import com.message.center.domain.vo.HandlerResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 消息验证处理器单元测试
 */
@ExtendWith(MockitoExtension.class)
class ValidationHandlerTest {

    private ValidationHandler validationHandler;

    @BeforeEach
    void setUp() {
        validationHandler = new ValidationHandler();
    }

    /**
     * 测试验证通过的情况
     */
    @Test
    void testHandle_Success() {
        // 构建测试上下文
        MessageContext context = new MessageContext();
        context.setTenantId(1001L);
        context.setBusinessType("ORDER_NOTIFY");
        context.setBusinessData("{\"orderId\":\"ORD123456\"}");

        // 执行测试
        HandlerResult result = validationHandler.handle(context);

        // 验证结果
        assertTrue(result.isSuccess());
        assertTrue(result.isContinueChain());
        assertNull(result.getErrorCode());
        assertNull(result.getErrorMessage());
    }

    /**
     * 测试缺少租户ID的情况
     */
    @Test
    void testHandle_Fail_MissingTenantId() {
        // 构建测试上下文（缺少租户ID）
        MessageContext context = new MessageContext();
        context.setBusinessType("ORDER_NOTIFY");
        context.setBusinessData("{\"orderId\":\"ORD123456\"}");

        // 执行测试
        HandlerResult result = validationHandler.handle(context);

        // 验证结果
        assertFalse(result.isSuccess());
        assertFalse(result.isContinueChain());
        assertEquals("VALIDATION_ERROR", result.getErrorCode());
        assertEquals("租户ID不能为空", result.getErrorMessage());
    }

    /**
     * 测试缺少业务类型的情况
     */
    @Test
    void testHandle_Fail_MissingBusinessType() {
        // 构建测试上下文（缺少业务类型）
        MessageContext context = new MessageContext();
        context.setTenantId(1001L);
        context.setBusinessData("{\"orderId\":\"ORD123456\"}");

        // 执行测试
        HandlerResult result = validationHandler.handle(context);

        // 验证结果
        assertFalse(result.isSuccess());
        assertFalse(result.isContinueChain());
        assertEquals("VALIDATION_ERROR", result.getErrorCode());
        assertEquals("业务类型不能为空", result.getErrorMessage());
    }

    /**
     * 测试缺少业务数据的情况
     */
    @Test
    void testHandle_Fail_MissingBusinessData() {
        // 构建测试上下文（缺少业务数据）
        MessageContext context = new MessageContext();
        context.setTenantId(1001L);
        context.setBusinessType("ORDER_NOTIFY");

        // 执行测试
        HandlerResult result = validationHandler.handle(context);

        // 验证结果
        assertFalse(result.isSuccess());
        assertFalse(result.isContinueChain());
        assertEquals("VALIDATION_ERROR", result.getErrorCode());
        assertEquals("业务数据不能为空", result.getErrorMessage());
    }

    /**
     * 测试业务数据格式错误的情况
     */
    @Test
    void testHandle_Fail_InvalidBusinessData() {
        // 构建测试上下文（业务数据格式错误）
        MessageContext context = new MessageContext();
        context.setTenantId(1001L);
        context.setBusinessType("ORDER_NOTIFY");
        context.setBusinessData("invalid json");

        // 执行测试
        HandlerResult result = validationHandler.handle(context);

        // 验证结果
        assertFalse(result.isSuccess());
        assertFalse(result.isContinueChain());
        assertEquals("VALIDATION_ERROR", result.getErrorCode());
        assertEquals("业务数据必须是有效的JSON格式", result.getErrorMessage());
    }

    /**
     * 测试获取处理器顺序
     */
    @Test
    void testGetOrder() {
        assertEquals(100, validationHandler.getOrder());
    }

    /**
     * 测试获取处理器名称
     */
    @Test
    void testGetName() {
        assertEquals("消息验证处理器", validationHandler.getName());
    }

    /**
     * 测试支持处理的情况
     */
    @Test
    void testSupports() {
        MessageContext context = new MessageContext();
        assertTrue(validationHandler.supports(context));
    }
}
