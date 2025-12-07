# 消息中心系统数据库设计

## 1. 数据库表结构

### 1.1 渠道配置表（msg_channel_config）

```sql
CREATE TABLE `msg_channel_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `store_id` bigint(20) DEFAULT NULL COMMENT '门店ID（空表示租户级）',
  `channel_type` varchar(32) NOT NULL COMMENT '渠道类型：DINGTALK/WECHAT_WORK/LOCAL',
  `channel_name` varchar(64) NOT NULL COMMENT '渠道名称',
  `config_json` text NOT NULL COMMENT '渠道配置JSON（认证信息、webhook等）',
  `priority` int(11) DEFAULT '1' COMMENT '优先级（数值越小优先级越高）',
  `enabled` tinyint(4) DEFAULT '1' COMMENT '是否启用',
  `rate_limit_count` int(11) DEFAULT '100' COMMENT '频次限制数量',
  `rate_limit_window` int(11) DEFAULT '60' COMMENT '频次限制时间窗口（秒）',
  `rate_limit_unit` varchar(10) DEFAULT 'SECOND' COMMENT '时间单位：SECOND/MINUTE/HOUR/DAY',
  `time_window_enabled` tinyint(4) DEFAULT '0' COMMENT '是否启用时间窗口限制',
  `time_window_start_hour` int(11) DEFAULT '0' COMMENT '允许发送开始时间（小时，0-23）',
  `time_window_end_hour` int(11) DEFAULT '23' COMMENT '允许发送结束时间（小时，0-23）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_store` (`tenant_id`,`store_id`),
  KEY `idx_channel_type` (`channel_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='渠道配置表';
```

### 1.2 消息模板表（msg_template）

```sql
CREATE TABLE `msg_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `business_type` varchar(64) NOT NULL COMMENT '业务类型',
  `channel_type` varchar(32) NOT NULL COMMENT '渠道类型：DINGTALK/WECHAT_WORK/LOCAL',
  `message_type` varchar(32) NOT NULL COMMENT '消息类型：TEXT/MARKDOWN/CARD/LINK',
  `template_name` varchar(64) NOT NULL COMMENT '模板名称',
  `template_content` text NOT NULL COMMENT '模板内容（使用${variableName}占位符）',
  `enabled` tinyint(4) DEFAULT '1' COMMENT '是否启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_business` (`tenant_id`,`business_type`),
  KEY `idx_channel_type` (`channel_type`),
  KEY `idx_message_type` (`message_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='消息模板表';
```

### 1.3 消息主表（msg_message）

```sql
CREATE TABLE `msg_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `message_id` varchar(64) NOT NULL COMMENT '消息唯一ID',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `store_id` bigint(20) DEFAULT NULL COMMENT '门店ID（空表示租户级）',
  `business_type` varchar(64) NOT NULL COMMENT '业务类型',
  `business_data` json NOT NULL COMMENT '业务数据JSON',
  `target_channels` varchar(255) DEFAULT NULL COMMENT '目标渠道列表',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '消息状态：PENDING/PROCESSING/SUCCESS/PARTIAL_SUCCESS/FAILED',
  `total_channels` int(11) DEFAULT '0' COMMENT '总渠道数',
  `success_channels` int(11) DEFAULT '0' COMMENT '成功渠道数',
  `failed_channels` int(11) DEFAULT '0' COMMENT '失败渠道数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（分区键）',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`id`,`create_time`),
  UNIQUE KEY `uk_message_id` (`message_id`,`create_time`),
  KEY `idx_tenant_store` (`tenant_id`,`store_id`),
  KEY `idx_business_type` (`business_type`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='消息主表' 
PARTITION BY RANGE (TO_DAYS(create_time)) (
  PARTITION p202501 VALUES LESS THAN (TO_DAYS('2025-02-01')),
  PARTITION p202502 VALUES LESS THAN (TO_DAYS('2025-03-01')),
  PARTITION p202503 VALUES LESS THAN (TO_DAYS('2025-04-01')),
  PARTITION p202504 VALUES LESS THAN (TO_DAYS('2025-05-01')),
  PARTITION p202505 VALUES LESS THAN (TO_DAYS('2025-06-01')),
  PARTITION p202506 VALUES LESS THAN (TO_DAYS('2025-07-01')),
  PARTITION p202507 VALUES LESS THAN (TO_DAYS('2025-08-01')),
  PARTITION p202508 VALUES LESS THAN (TO_DAYS('2025-09-01')),
  PARTITION p202509 VALUES LESS THAN (TO_DAYS('2025-10-01')),
  PARTITION p202510 VALUES LESS THAN (TO_DAYS('2025-11-01')),
  PARTITION p202511 VALUES LESS THAN (TO_DAYS('2025-12-01')),
  PARTITION p202512 VALUES LESS THAN (TO_DAYS('2026-01-01')),
  PARTITION p_future VALUES LESS THAN MAXVALUE
);
```

### 1.4 渠道任务表（msg_channel_task）

```sql
CREATE TABLE `msg_channel_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `message_id` varchar(64) NOT NULL COMMENT '消息ID',
  `channel_type` varchar(32) NOT NULL COMMENT '渠道类型',
  `channel_config_id` bigint(20) NOT NULL COMMENT '渠道配置ID',
  `rendered_content` text DEFAULT NULL COMMENT '渲染后的消息内容',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/SENDING/SUCCESS/FAILED/RETRY',
  `retry_count` int(11) DEFAULT '0' COMMENT '已重试次数',
  `max_retry` int(11) DEFAULT '3' COMMENT '最大重试次数',
  `next_retry_time` datetime DEFAULT NULL COMMENT '下次重试时间',
  `result_message` varchar(500) DEFAULT NULL COMMENT '发送结果消息',
  `third_party_response` text DEFAULT NULL COMMENT '第三方响应内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（分区键）',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`id`,`create_time`),
  KEY `idx_message_id` (`message_id`),
  KEY `idx_status` (`status`),
  KEY `idx_next_retry` (`next_retry_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='渠道发送任务表' 
PARTITION BY RANGE (TO_DAYS(create_time)) (
  PARTITION p202501 VALUES LESS THAN (TO_DAYS('2025-02-01')),
  PARTITION p202502 VALUES LESS THAN (TO_DAYS('2025-03-01')),
  PARTITION p202503 VALUES LESS THAN (TO_DAYS('2025-04-01')),
  PARTITION p202504 VALUES LESS THAN (TO_DAYS('2025-05-01')),
  PARTITION p202505 VALUES LESS THAN (TO_DAYS('2025-06-01')),
  PARTITION p202506 VALUES LESS THAN (TO_DAYS('2025-07-01')),
  PARTITION p202507 VALUES LESS THAN (TO_DAYS('2025-08-01')),
  PARTITION p202508 VALUES LESS THAN (TO_DAYS('2025-09-01')),
  PARTITION p202509 VALUES LESS THAN (TO_DAYS('2025-10-01')),
  PARTITION p202510 VALUES LESS THAN (TO_DAYS('2025-11-01')),
  PARTITION p202511 VALUES LESS THAN (TO_DAYS('2025-12-01')),
  PARTITION p202512 VALUES LESS THAN (TO_DAYS('2026-01-01')),
  PARTITION p_future VALUES LESS THAN MAXVALUE
);
```

## 2. 数据库索引优化

### 2.1 索引设计原则

- 为频繁查询的字段创建索引
- 为外键字段创建索引
- 为排序字段创建索引
- 避免创建过多索引，影响写入性能
- 合理设计复合索引，考虑最左前缀原则

### 2.2 索引设计

| 表名 | 索引字段 | 索引类型 | 用途 |
|------|----------|----------|------|
| msg_channel_config | tenant_id, store_id | 复合索引 | 按租户和门店查询渠道配置 |
| msg_channel_config | channel_type | 单字段索引 | 按渠道类型查询 |
| msg_template | tenant_id, business_type | 复合索引 | 按租户和业务类型查询模板 |
| msg_template | channel_type | 单字段索引 | 按渠道类型查询模板 |
| msg_message | message_id, create_time | 唯一索引 | 按消息ID查询 |
| msg_message | tenant_id, store_id | 复合索引 | 按租户和门店查询消息 |
| msg_message | business_type | 单字段索引 | 按业务类型查询消息 |
| msg_message | status | 单字段索引 | 按状态查询消息 |
| msg_message | create_time | 单字段索引 | 按时间查询消息 |
| msg_channel_task | message_id | 单字段索引 | 按消息ID查询任务 |
| msg_channel_task | status | 单字段索引 | 按状态查询任务 |
| msg_channel_task | next_retry_time | 单字段索引 | 查询待重试任务 |

## 3. 分区表管理

### 3.1 分区表优势

- 提高查询性能：通过分区裁剪，只查询相关分区
- 便于历史数据管理：可以快速删除或归档过期分区
- 提高写入性能：并发写入不同分区

### 3.2 分区管理策略

- **添加新分区**：每月自动添加下一个月的分区
- **删除旧分区**：保留最近12个月数据，自动删除超过12个月的分区
- **分区维护脚本**：使用定时任务执行分区维护

### 3.3 分区维护示例

```sql
-- 添加新分区
ALTER TABLE msg_message ADD PARTITION (
    PARTITION p202601 VALUES LESS THAN (TO_DAYS('2026-02-01'))
);

-- 删除旧分区
ALTER TABLE msg_message DROP PARTITION p202501;
```

## 4. 数据迁移与备份策略

### 4.1 数据迁移

- 使用MyBatis-Plus的代码生成器生成基础代码
- 编写数据迁移脚本，初始化基础数据
- 使用Flyway或Liquibase管理数据库版本

### 4.2 备份策略

- 每日全量备份
- 每小时增量备份
- 备份数据存储在独立的存储设备上
- 定期测试备份数据的可恢复性

## 5. 数据库连接配置

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/message_center?useUnicode=true&characterEncoding=utf8mb4&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: password
    type: com.alibaba.druid.pool.DruidDataSource
    druid:
      initial-size: 5
      min-idle: 5
      max-active: 20
      max-wait: 60000
      time-between-eviction-runs-millis: 60000
      min-evictable-idle-time-millis: 300000
      validation-query: SELECT 1 FROM DUAL
      test-while-idle: true
      test-on-borrow: false
      test-on-return: false
      pool-prepared-statements: true
      max-pool-prepared-statement-per-connection-size: 20
      filters: stat,wall,slf4j
      connection-properties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000
```

## 6. 性能优化建议

### 6.1 查询优化

- 使用分区表，按时间范围查询时利用分区裁剪
- 合理使用索引，避免全表扫描
- 减少不必要的字段查询，只查询需要的字段
- 使用分页查询，避免一次性查询大量数据

### 6.2 写入优化

- 批量插入数据，减少数据库连接次数
- 合理设置事务隔离级别，避免不必要的锁竞争
- 使用异步写入，提高系统响应速度

### 6.3 并发优化

- 使用读写分离，提高并发处理能力
- 合理设置数据库连接池参数
- 避免长事务，减少锁持有时间

## 7. 安全性考虑

### 7.1 数据加密

- 敏感数据加密存储
- 数据库连接使用SSL加密
- 定期更换数据库密码

### 7.2 访问控制

- 最小权限原则，只授予必要的数据库权限
- 限制数据库访问IP
- 定期审计数据库访问日志

### 7.3 数据脱敏

- 查询结果中的敏感数据脱敏处理
- 日志中的敏感数据脱敏处理

## 8. 监控与告警

### 8.1 监控指标

- 数据库连接数
- 查询响应时间
- 慢查询数量
- 写入吞吐量
- 磁盘使用情况

### 8.2 告警策略

- 连接数超过阈值时告警
- 慢查询数量超过阈值时告警
- 磁盘使用率超过阈值时告警
- 数据库错误日志中出现异常时告警

## 9. 高可用性设计

### 9.1 数据库集群

- 采用MySQL主从架构，实现读写分离
- 主库故障时，从库可以快速切换为主库
- 使用Keepalived或ProxySQL实现高可用

### 9.2 数据冗余

- 重要数据多副本存储
- 定期数据校验，确保数据一致性
- 实现数据恢复机制，确保数据可恢复

## 10. 容灾设计

### 10.1 异地备份

- 定期将数据备份到异地存储
- 建立异地灾备中心，确保在本地数据中心故障时可以快速恢复

### 10.2 灾备演练

- 定期进行灾备演练，验证灾备方案的可行性
- 不断优化灾备方案，提高灾备效率

## 11. 总结

本数据库设计方案详细描述了消息中心系统的数据库表结构、索引设计、分区表管理、数据迁移与备份策略、性能优化建议、安全性考虑、监控与告警、高可用性设计和容灾设计。通过合理的数据库设计，可以确保消息中心系统的高性能、高可用性和高可靠性，满足各种业务场景的需求。