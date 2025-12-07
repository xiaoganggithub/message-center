package com.message.center.service;

import com.message.center.domain.vo.Message;
import com.message.center.domain.vo.SendResult;
import com.message.center.service.api.MessageSendService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 消息发送集成测试
 */
@SpringBootTest
@ActiveProfiles("test")
class MessageSendIntegrationTest {

    @Autowired
    private MessageSendService messageSendService;

    /**
     * 测试发送单条消息的完整流程
     */
    @Test
    void testSendMessage() {
        // 构建测试消息
        Message message = new Message();
        message.setTenantId(1001L);
        message.setBusinessType("ORDER_NOTIFY");
        message.setBusinessData("{\"orderId\":\"ORD123456\",\"customerName\":\"张三\",\"amount\":199.0}");

        // 执行测试
        SendResult result = messageSendService.sendMessage(message);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getMessageId());
        assertNull(result.getErrorCode());
        assertNull(result.getErrorMessage());
    }

    /**
     * 测试批量发送消息的完整流程
     */
    @Test
    void testBatchSendMessages() {
        // 构建测试消息列表
        Message message1 = new Message();
        message1.setTenantId(1001L);
        message1.setBusinessType("ORDER_NOTIFY");
        message1.setBusinessData("{\"orderId\":\"ORD123456\",\"customerName\":\"张三\",\"amount\":199.0}");

        Message message2 = new Message();
        message2.setTenantId(1001L);
        message2.setBusinessType("NOTIFY");
        message2.setBusinessData("{\"content\":\"测试通知"}");

        // 执行测试
        MessageSendService.BatchSendResult result = messageSendService.batchSendMessages(java.util.List.of(message1, message2));

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.getTotalCount());
        assertEquals(2, result.getSuccessCount());
        assertEquals(0, result.getFailedCount());
        assertNotNull(result.getResults());
        assertEquals(2, result.getResults().size());

        // 验证每条消息的发送结果
        for (SendResult sendResult : result.getResults()) {
            assertTrue(sendResult.isSuccess());
            assertNotNull(sendResult.getMessageId());
        }
    }
}
