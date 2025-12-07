-- 消息中心数据库初始化脚本
-- 创建时间：2023-12-06

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS message_center 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_bin;

-- 使用数据库
USE message_center;

-- 1. 渠道配置表
CREATE TABLE IF NOT EXISTS msg_channel_config (
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

-- 2. 消息模板表
CREATE TABLE IF NOT EXISTS msg_template (
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

-- 3. 消息主表（按时间分区）
CREATE TABLE IF NOT EXISTS msg_message (
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

-- 4. 渠道发送任务表（按时间分区）
CREATE TABLE IF NOT EXISTS msg_channel_task (
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

-- 初始化基础数据

-- 1. 初始化渠道配置
INSERT INTO msg_channel_config (
  tenant_id, store_id, channel_type, channel_name, config_json, 
  priority, enabled, rate_limit_count, rate_limit_window, rate_limit_unit,
  time_window_enabled, time_window_start_hour, time_window_end_hour
) VALUES
(1001, NULL, 'LOCAL', '本地消息', '{}', 
  1, 1, 100, 60, 'SECOND',
  0, 0, 23),
(1001, NULL, 'DINGTALK', '钉钉机器人', '{"webhook": "https://oapi.dingtalk.com/robot/send?access_token=test_token", "secret": "test_secret"}', 
  1, 1, 100, 60, 'SECOND',
  0, 0, 23),
(1001, NULL, 'WECHAT_WORK', '企业微信群机器人', '{"webhook": "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=test_key"}', 
  1, 1, 100, 60, 'SECOND',
  0, 0, 23);

-- 2. 初始化消息模板
INSERT INTO msg_template (
  tenant_id, business_type, channel_type, message_type, 
  template_name, template_content, enabled
) VALUES
(1001, 'ORDER_NOTIFY', 'LOCAL', 'TEXT', 
  '订单通知模板', '您有一笔新订单：${orderId}，客户：${customerName}，金额：${amount}元', 1),
(1001, 'ORDER_NOTIFY', 'DINGTALK', 'TEXT', 
  '订单通知模板', '您有一笔新订单：${orderId}，客户：${customerName}，金额：${amount}元', 1),
(1001, 'ORDER_NOTIFY', 'WECHAT_WORK', 'TEXT', 
  '订单通知模板', '您有一笔新订单：${orderId}，客户：${customerName}，金额：${amount}元', 1),
(1001, 'NOTIFY', 'LOCAL', 'TEXT', 
  '通用通知模板', '通知：${content}', 1),
(1001, 'NOTIFY', 'DINGTALK', 'TEXT', 
  '通用通知模板', '通知：${content}', 1),
(1001, 'NOTIFY', 'WECHAT_WORK', 'TEXT', 
  '通用通知模板', '通知：${content}', 1);

-- 初始化完成
SELECT '数据库初始化完成' AS result;