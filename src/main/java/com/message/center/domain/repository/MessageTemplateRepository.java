package com.message.center.domain.repository;

import com.message.center.domain.entity.MessageTemplate;

/**
 * 消息模板仓库接口
 */
public interface MessageTemplateRepository {

    /**
     * 根据模板ID查询消息模板
     * @param templateId 模板ID
     * @return 消息模板对象
     */
    MessageTemplate getById(Long templateId);
}