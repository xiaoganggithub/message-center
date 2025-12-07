# DDD重构计划

## 1. 当前项目分析

当前项目已经实现了消息中心的核心功能，但在结构上还没有完全遵循DDD原则。主要问题包括：
- 领域逻辑与基础设施代码混合
- 实体和值对象的区分不明确
- 缺少清晰的领域服务
- 仓库模式实现不够规范

## 2. DDD分层架构设计

### 2.1 分层结构

```
└── src/main/java/com/message/center
    ├── application                 # 应用层
    │   ├── command                 # 命令处理
    │   ├── query                   # 查询处理
    │   └── service                 # 应用服务
    ├── domain                      # 领域层
    │   ├── entity                  # 实体
    │   ├── valueobject             # 值对象
    │   ├── service                 # 领域服务
    │   ├── repository              # 仓库接口
    │   └── event                   # 领域事件
    ├── infrastructure              # 基础设施层
    │   ├── data                    # 数据访问
    │   │   ├── mapper              # MyBatis映射
    │   │   └── repository          # 仓库实现
    │   ├── config                  # 配置
    │   ├── adapter                 # 外部系统适配器
    │   └── executor                # 执行器
    ├── interfaces                  # 接口层
    │   ├── rest                    # REST接口
    │   └── dubbo                   # Dubbo接口
    └── MessageCenterApplication.java # 应用入口
```

### 2.2 有界上下文

消息中心系统主要包含一个有界上下文：**消息发送上下文**

### 2.3 核心领域模型

- **聚合根**：Message
- **实体**：ChannelTask
- **值对象**：ChannelConfig, MessageTemplate, ChannelType, MessageStatus, TaskStatus
- **领域服务**：MessageSendService, TemplateRenderService
- **仓库**：MessageRepository, ChannelTaskRepository, ChannelConfigRepository, MessageTemplateRepository

## 3. 重构步骤

### 3.1 第一步：创建DDD目录结构

### 3.2 第二步：重构领域模型
- 将实体和值对象分离
- 确保实体具有唯一标识
- 实现值对象的不可变性

### 3.3 第三步：重构仓库模式
- 定义清晰的仓库接口
- 实现仓库的基础设施层实现

### 3.4 第四步：实现领域服务
- 提取领域逻辑到领域服务
- 确保领域服务不依赖基础设施

### 3.5 第五步：重构应用层
- 实现命令和查询分离
- 实现应用服务

### 3.6 第六步：重构接口层
- 将REST和Dubbo接口统一到接口层

### 3.7 第七步：重构基础设施层
- 将适配器、配置、执行器等统一到基础设施层

### 3.8 第八步：测试验证
- 运行单元测试
- 运行集成测试
- 验证功能完整性

## 4. 重构开始

首先，我将创建DDD目录结构，然后逐步重构各个模块。