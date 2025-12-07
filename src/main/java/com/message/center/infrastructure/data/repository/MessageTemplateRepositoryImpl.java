package com.message.center.infrastructure.data.repository;

import com.message.center.domain.entity.MessageTemplate;
import com.message.center.domain.repository.MessageTemplateRepository;
import com.message.center.infrastructure.data.mapper.MessageTemplateMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 消息模板仓库实现
 */
@Repository
@Slf4j
public class MessageTemplateRepositoryImpl implements MessageTemplateRepository {

    @Autowired
    private MessageTemplateMapper messageTemplateMapper;

    @Override
    public MessageTemplate getById(Long templateId) {
        return messageTemplateMapper.selectById(templateId);
    }
}