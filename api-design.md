# 消息中心系统API设计

## 1. 概述

### 1.1 API分类

消息中心系统提供以下几类API：

- **Dubbo API**：主要用于内部服务调用，提供高性能的消息发送能力
- **REST API**：主要用于外部系统调用，包括消息发送、配置管理、消息查询等功能

### 1.2 API设计原则

- **统一标准**：所有API遵循统一的设计规范
- **简单易用**：API设计简洁明了，易于理解和使用
- **高可用性**：API具有良好的容错机制和重试策略
- **安全性**：API提供身份验证和授权机制
- **可扩展性**：API设计具有良好的扩展性，支持新增功能和渠道

## 2. Dubbo API设计

### 2.1 消息发送服务

#### 2.1.1 接口定义

```java
/**
 * 消息发送服务
 */
public interface MessageSendService {
    /**
     * 发送单条消息
     * @param message 消息对象
     * @return 发送结果
     */
    SendResult sendMessage(Message message);

    /**
     * 批量发送消息
     * @param messages 消息列表
     * @return 批量发送结果
     */
    BatchSendResult batchSendMessages(List<Message> messages);
}
```

#### 2.1.2 数据模型

```java
/**
 * 消息对象
 */
@Data
public class Message {
    /** 消息ID（可选，系统自动生成） */
    private String messageId;
    /** 租户ID */
    private Long tenantId;
    /** 门店ID（可选） */
    private Long storeId;
    /** 业务类型 */
    private String businessType;
    /** 业务数据（JSON格式） */
    private String businessData;
    /** 目标渠道列表（可选，不指定则使用配置的渠道） */
    private List<ChannelType> targetChannels;
    /** 扩展属性 */
    private Map<String, Object> attributes;
}

/**
 * 发送结果
 */
@Data
public class SendResult {
    /** 是否成功 */
    private boolean success;
    /** 消息ID */
    private String messageId;
    /** 错误码 */
    private String errorCode;
    /** 错误信息 */
    private String errorMessage;
}

/**
 * 批量发送结果
 */
@Data
public class BatchSendResult {
    /** 总消息数 */
    private int totalCount;
    /** 成功数 */
    private int successCount;
    /** 失败数 */
    private int failedCount;
    /** 详细结果列表 */
    private List<SendResult> results;
}
```

#### 2.1.3 调用示例

```java
// 发送单条消息
Message message = new Message();
message.setTenantId(1001L);
message.setBusinessType("ORDER_NOTIFY");
message.setBusinessData("{\"orderId\":\"ORD123456\",\"customerName\":\"张三\",\"amount\":199.0}");
SendResult result = messageSendService.sendMessage(message);

// 批量发送消息
List<Message> messages = new ArrayList<>();
messages.add(message1);
messages.add(message2);
BatchSendResult batchResult = messageSendService.batchSendMessages(messages);
```

### 2.2 消息查询服务

#### 2.2.1 接口定义

```java
/**
 * 消息查询服务
 */
public interface MessageQueryService {
    /**
     * 根据消息ID查询消息
     * @param messageId 消息ID
     * @return 消息详情
     */
    MessageDetail queryMessageById(String messageId);

    /**
     * 分页查询消息
     * @param query 查询条件
     * @return 分页结果
     */
    PageResult<MessageDetail> queryMessages(MessageQuery query);

    /**
     * 查询消息的渠道任务
     * @param messageId 消息ID
     * @return 渠道任务列表
     */
    List<ChannelTaskDetail> queryChannelTasks(String messageId);
}
```

#### 2.2.2 数据模型

```java
/**
 * 消息查询条件
 */
@Data
public class MessageQuery {
    /** 消息ID */
    private String messageId;
    /** 租户ID */
    private Long tenantId;
    /** 门店ID */
    private Long storeId;
    /** 业务类型 */
    private String businessType;
    /** 消息状态 */
    private MessageStatus status;
    /** 开始时间 */
    private Date startTime;
    /** 结束时间 */
    private Date endTime;
    /** 分页页码 */
    private Integer pageNum;
    /** 分页大小 */
    private Integer pageSize;
}

/**
 * 消息详情
 */
@Data
public class MessageDetail {
    /** 消息ID */
    private String messageId;
    /** 租户ID */
    private Long tenantId;
    /** 门店ID */
    private Long storeId;
    /** 业务类型 */
    private String businessType;
    /** 业务数据 */
    private String businessData;
    /** 目标渠道列表 */
    private List<ChannelType> targetChannels;
    /** 消息状态 */
    private MessageStatus status;
    /** 总渠道数 */
    private Integer totalChannels;
    /** 成功渠道数 */
    private Integer successChannels;
    /** 失败渠道数 */
    private Integer failedChannels;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;
    /** 完成时间 */
    private Date finishTime;
}

/**
 * 渠道任务详情
 */
@Data
public class ChannelTaskDetail {
    /** 任务ID */
    private Long id;
    /** 消息ID */
    private String messageId;
    /** 渠道类型 */
    private ChannelType channelType;
    /** 渠道名称 */
    private String channelName;
    /** 渲染后的消息内容 */
    private String renderedContent;
    /** 任务状态 */
    private TaskStatus status;
    /** 已重试次数 */
    private Integer retryCount;
    /** 最大重试次数 */
    private Integer maxRetry;
    /** 下次重试时间 */
    private Date nextRetryTime;
    /** 结果消息 */
    private String resultMessage;
    /** 第三方响应 */
    private String thirdPartyResponse;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;
    /** 完成时间 */
    private Date finishTime;
}
```

## 3. REST API设计

### 3.1 消息发送API

#### 3.1.1 发送单条消息

- **URL**：`/api/messages`
- **Method**：`POST`
- **Description**：发送单条消息
- **Request Body**：

```json
{
  "tenantId": 1001,
  "storeId": 2001,
  "businessType": "ORDER_NOTIFY",
  "businessData": {
    "orderId": "ORD123456",
    "customerName": "张三",
    "amount": 199.0
  },
  "targetChannels": ["DINGTALK", "WECHAT_WORK"],
  "attributes": {
    "key1": "value1",
    "key2": "value2"
  }
}
```

- **Response**：

```json
{
  "code": "200",
  "message": "success",
  "data": {
    "success": true,
    "messageId": "MSG202312060001",
    "errorCode": null,
    "errorMessage": null
  }
}
```

#### 3.1.2 批量发送消息

- **URL**：`/api/messages/batch`
- **Method**：`POST`
- **Description**：批量发送消息
- **Request Body**：

```json
[
  {
    "tenantId": 1001,
    "businessType": "ORDER_NOTIFY",
    "businessData": {
      "orderId": "ORD123456",
      "customerName": "张三",
      "amount": 199.0
    }
  },
  {
    "tenantId": 1001,
    "businessType": "ORDER_NOTIFY",
    "businessData": {
      "orderId": "ORD123457",
      "customerName": "李四",
      "amount": 299.0
    }
  }
]
```

- **Response**：

```json
{
  "code": "200",
  "message": "success",
  "data": {
    "totalCount": 2,
    "successCount": 2,
    "failedCount": 0,
    "results": [
      {
        "success": true,
        "messageId": "MSG202312060001",
        "errorCode": null,
        "errorMessage": null
      },
      {
        "success": true,
        "messageId": "MSG202312060002",
        "errorCode": null,
        "errorMessage": null
      }
    ]
  }
}
```

#### 3.1.3 查询消息详情

- **URL**：`/api/messages/{messageId}`
- **Method**：`GET`
- **Description**：查询消息详情
- **Response**：

```json
{
  "code": "200",
  "message": "success",
  "data": {
    "messageId": "MSG202312060001",
    "tenantId": 1001,
    "storeId": 2001,
    "businessType": "ORDER_NOTIFY",
    "businessData": {
      "orderId": "ORD123456",
      "customerName": "张三",
      "amount": 199.0
    },
    "targetChannels": ["DINGTALK", "WECHAT_WORK"],
    "status": "SUCCESS",
    "totalChannels": 2,
    "successChannels": 2,
    "failedChannels": 0,
    "createTime": "2023-12-06T10:00:00",
    "updateTime": "2023-12-06T10:00:05",
    "finishTime": "2023-12-06T10:00:05"
  }
}
```

#### 3.1.4 查询消息列表

- **URL**：`/api/messages`
- **Method**：`GET`
- **Description**：分页查询消息列表
- **Query Parameters**：
  - `messageId`：消息ID
  - `tenantId`：租户ID
  - `storeId`：门店ID
  - `businessType`：业务类型
  - `status`：消息状态
  - `startTime`：开始时间（格式：yyyy-MM-dd HH:mm:ss）
  - `endTime`：结束时间（格式：yyyy-MM-dd HH:mm:ss）
  - `pageNum`：页码（默认：1）
  - `pageSize`：每页大小（默认：10）

- **Response**：

```json
{
  "code": "200",
  "message": "success",
  "data": {
    "total": 100,
    "pages": 10,
    "pageNum": 1,
    "pageSize": 10,
    "list": [
      {
        "messageId": "MSG202312060001",
        "tenantId": 1001,
        "storeId": 2001,
        "businessType": "ORDER_NOTIFY",
        "status": "SUCCESS",
        "totalChannels": 2,
        "successChannels": 2,
        "failedChannels": 0,
        "createTime": "2023-12-06T10:00:00",
        "finishTime": "2023-12-06T10:00:05"
      }
    ]
  }
}
```

#### 3.1.5 查询渠道任务

- **URL**：`/api/messages/{messageId}/tasks`
- **Method**：`GET`
- **Description**：查询消息的渠道任务
- **Response**：

```json
{
  "code": "200",
  "message": "success",
  "data": [
    {
      "id": 1,
      "messageId": "MSG202312060001",
      "channelType": "DINGTALK",
      "channelName": "钉钉机器人",
      "renderedContent": "{\"msgtype\":\"text\",\"text\":{\"content\":\"您有一笔新订单：ORD123456，金额：199.0元\"}}",
      "status": "SUCCESS",
      "retryCount": 0,
      "maxRetry": 3,
      "resultMessage": "发送成功",
      "thirdPartyResponse": "{\"errcode\":0,\"errmsg\":\"ok\"}",
      "createTime": "2023-12-06T10:00:00",
      "finishTime": "2023-12-06T10:00:02"
    },
    {
      "id": 2,
      "messageId": "MSG202312060001",
      "channelType": "WECHAT_WORK",
      "channelName": "企业微信群机器人",
      "renderedContent": "{\"msgtype\":\"text\",\"text\":{\"content\":\"您有一笔新订单：ORD123456，金额：199.0元\"}}",
      "status": "SUCCESS",
      "retryCount": 0,
      "maxRetry": 3,
      "resultMessage": "发送成功",
      "thirdPartyResponse": "{\"errcode\":0,\"errmsg\":\"ok\"}",
      "createTime": "2023-12-06T10:00:00",
      "finishTime": "2023-12-06T10:00:03"
    }
  ]
}
```

### 3.2 渠道配置管理API

#### 3.2.1 创建渠道配置

- **URL**：`/api/channel-configs`
- **Method**：`POST`
- **Description**：创建渠道配置
- **Request Body**：

```json
{
  "tenantId": 1001,
  "storeId": 2001,
  "channelType": "DINGTALK",
  "channelName": "钉钉机器人",
  "configJson": {
    "webhook": "https://oapi.dingtalk.com/robot/send?access_token=xxx",
    "secret": "xxx"
  },
  "priority": 1,
  "enabled": true,
  "rateLimitCount": 100,
  "rateLimitWindow": 60,
  "rateLimitUnit": "SECOND",
  "timeWindowEnabled": false,
  "timeWindowStartHour": 0,
  "timeWindowEndHour": 23
}
```

- **Response**：

```json
{
  "code": "200",
  "message": "success",
  "data": {
    "id": 1,
    "tenantId": 1001,
    "storeId": 2001,
    "channelType": "DINGTALK",
    "channelName": "钉钉机器人",
    "configJson": {
      "webhook": "https://oapi.dingtalk.com/robot/send?access_token=xxx",
      "secret": "xxx"
    },
    "priority": 1,
    "enabled": true,
    "rateLimitCount": 100,
    "rateLimitWindow": 60,
    "rateLimitUnit": "SECOND",
    "timeWindowEnabled": false,
    "timeWindowStartHour": 0,
    "timeWindowEndHour": 23,
    "createTime": "2023-12-06T10:00:00",
    "updateTime": "2023-12-06T10:00:00"
  }
}
```

#### 3.2.2 更新渠道配置

- **URL**：`/api/channel-configs/{id}`
- **Method**：`PUT`
- **Description**：更新渠道配置
- **Request Body**：同创建渠道配置
- **Response**：同创建渠道配置

#### 3.2.3 删除渠道配置

- **URL**：`/api/channel-configs/{id}`
- **Method**：`DELETE`
- **Description**：删除渠道配置
- **Response**：

```json
{
  "code": "200",
  "message": "success",
  "data": true
}
```

#### 3.2.4 查询渠道配置详情

- **URL**：`/api/channel-configs/{id}`
- **Method**：`GET`
- **Description**：查询渠道配置详情
- **Response**：同创建渠道配置

#### 3.2.5 查询渠道配置列表

- **URL**：`/api/channel-configs`
- **Method**：`GET`
- **Description**：分页查询渠道配置列表
- **Query Parameters**：
  - `tenantId`：租户ID
  - `storeId`：门店ID
  - `channelType`：渠道类型
  - `enabled`：是否启用
  - `pageNum`：页码（默认：1）
  - `pageSize`：每页大小（默认：10）

- **Response**：

```json
{
  "code": "200",
  "message": "success",
  "data": {
    "total": 10,
    "pages": 1,
    "pageNum": 1,
    "pageSize": 10,
    "list": [
      {
        "id": 1,
        "tenantId": 1001,
        "storeId": 2001,
        "channelType": "DINGTALK",
        "channelName": "钉钉机器人",
        "priority": 1,
        "enabled": true,
        "createTime": "2023-12-06T10:00:00",
        "updateTime": "2023-12-06T10:00:00"
      }
    ]
  }
}
```

### 3.3 模板配置管理API

#### 3.3.1 创建消息模板

- **URL**：`/api/templates`
- **Method**：`POST`
- **Description**：创建消息模板
- **Request Body**：

```json
{
  "tenantId": 1001,
  "businessType": "ORDER_NOTIFY",
  "channelType": "DINGTALK",
  "messageType": "TEXT",
  "templateName": "订单通知模板",
  "templateContent": "您有一笔新订单：${orderId}，金额：${amount}元",
  "enabled": true
}
```

- **Response**：

```json
{
  "code": "200",
  "message": "success",
  "data": {
    "id": 1,
    "tenantId": 1001,
    "businessType": "ORDER_NOTIFY",
    "channelType": "DINGTALK",
    "messageType": "TEXT",
    "templateName": "订单通知模板",
    "templateContent": "您有一笔新订单：${orderId}，金额：${amount}元",
    "enabled": true,
    "createTime": "2023-12-06T10:00:00",
    "updateTime": "2023-12-06T10:00:00"
  }
}
```

#### 3.3.2 更新消息模板

- **URL**：`/api/templates/{id}`
- **Method**：`PUT`
- **Description**：更新消息模板
- **Request Body**：同创建消息模板
- **Response**：同创建消息模板

#### 3.3.3 删除消息模板

- **URL**：`/api/templates/{id}`
- **Method**：`DELETE`
- **Description**：删除消息模板
- **Response**：

```json
{
  "code": "200",
  "message": "success",
  "data": true
}
```

#### 3.3.4 查询消息模板详情

- **URL**：`/api/templates/{id}`
- **Method**：`GET`
- **Description**：查询消息模板详情
- **Response**：同创建消息模板

#### 3.3.5 查询消息模板列表

- **URL**：`/api/templates`
- **Method**：`GET`
- **Description**：分页查询消息模板列表
- **Query Parameters**：
  - `tenantId`：租户ID
  - `businessType`：业务类型
  - `channelType`：渠道类型
  - `messageType`：消息类型
  - `enabled`：是否启用
  - `pageNum`：页码（默认：1）
  - `pageSize`：每页大小（默认：10）

- **Response**：

```json
{
  "code": "200",
  "message": "success",
  "data": {
    "total": 5,
    "pages": 1,
    "pageNum": 1,
    "pageSize": 10,
    "list": [
      {
        "id": 1,
        "tenantId": 1001,
        "businessType": "ORDER_NOTIFY",
        "channelType": "DINGTALK",
        "messageType": "TEXT",
        "templateName": "订单通知模板",
        "enabled": true,
        "createTime": "2023-12-06T10:00:00",
        "updateTime": "2023-12-06T10:00:00"
      }
    ]
  }
}
```

## 4. API错误码定义

| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| 200 | success | 成功 |
| 400 | Bad Request | 请求参数错误 |
| 401 | Unauthorized | 未授权 |
| 403 | Forbidden | 禁止访问 |
| 404 | Not Found | 资源不存在 |
| 500 | Internal Server Error | 服务器内部错误 |
| 1001 | VALIDATION_ERROR | 消息验证失败 |
| 1002 | NO_CHANNEL | 未配置可用的发送渠道 |
| 1003 | RATE_LIMITED | 触发频次限制 |
| 1004 | TEMPLATE_NOT_FOUND | 未找到匹配的消息模板 |
| 1005 | CHANNEL_ERROR | 渠道发送失败 |
| 1006 | TIMEOUT | 发送超时 |
| 1007 | DUPLICATE_MESSAGE | 消息重复 |
| 1008 | NOT_IN_TIME_WINDOW | 不在允许发送的时间窗口内 |

## 5. API安全设计

### 5.1 身份验证

- **Dubbo API**：采用令牌认证机制
- **REST API**：采用JWT认证机制

### 5.2 授权

- 基于角色的访问控制（RBAC）
- 每个API端点都有对应的权限控制

### 5.3 限流

- 对API调用进行限流，防止恶意请求
- 采用Redis实现分布式限流

### 5.4 加密

- API通信采用HTTPS加密
- 敏感数据在传输过程中加密

### 5.5 日志审计

- 记录所有API调用日志
- 包括调用者信息、请求参数、响应结果等
- 定期审计API调用日志

## 6. API监控与告警

### 6.1 监控指标

- API调用次数
- API响应时间
- API错误率
- API成功率

### 6.2 告警策略

- 响应时间超过阈值时告警
- 错误率超过阈值时告警
- 调用次数异常增长时告警

## 7. API版本管理

### 7.1 版本策略

- 采用URI版本控制，如：`/api/v1/messages`
- 向后兼容原则，新版本API兼容旧版本
- 旧版本API在适当时候废弃

### 7.2 版本迁移

- 提供清晰的版本迁移指南
- 旧版本API废弃前提前通知用户
- 提供足够的迁移时间

## 8. 示例代码

### 8.1 Java客户端示例

```java
// 使用RestTemplate调用REST API
RestTemplate restTemplate = new RestTemplate();
HttpHeaders headers = new HttpHeaders();
headers.setContentType(MediaType.APPLICATION_JSON);
headers.setBearerAuth("your-jwt-token");

// 构建请求体
Message message = new Message();
message.setTenantId(1001L);
message.setBusinessType("ORDER_NOTIFY");
message.setBusinessData("{\"orderId\":\"ORD123456\",\"customerName\":\"张三\",\"amount\":199.0}");

HttpEntity<Message> request = new HttpEntity<>(message, headers);
ResponseEntity<ApiResponse<SendResult>> response = restTemplate.postForEntity(
    "http://message-center/api/v1/messages", 
    request, 
    new ParameterizedTypeReference<ApiResponse<SendResult>>() {}
);

// 处理响应
if (response.getStatusCode() == HttpStatus.OK) {
    ApiResponse<SendResult> apiResponse = response.getBody();
    if (apiResponse != null && "200".equals(apiResponse.getCode())) {
        SendResult result = apiResponse.getData();
        System.out.println("消息发送成功，消息ID：" + result.getMessageId());
    } else {
        System.out.println("消息发送失败：" + apiResponse.getMessage());
    }
}
```

### 8.2 Dubbo客户端示例

```java
// 使用Dubbo调用消息发送服务
@Reference
private MessageSendService messageSendService;

public void sendMessage() {
    Message message = new Message();
    message.setTenantId(1001L);
    message.setBusinessType("ORDER_NOTIFY");
    message.setBusinessData("{\"orderId\":\"ORD123456\",\"customerName\":\"张三\",\"amount\":199.0}");
    
    SendResult result = messageSendService.sendMessage(message);
    if (result.isSuccess()) {
        System.out.println("消息发送成功，消息ID：" + result.getMessageId());
    } else {
        System.out.println("消息发送失败：" + result.getErrorMessage());
    }
}
```

## 9. 总结

本API设计文档详细描述了消息中心系统的API设计，包括Dubbo API和REST API。通过统一的API设计规范和良好的安全机制，确保消息中心系统能够提供高性能、高可用性、高安全性的消息发送服务。