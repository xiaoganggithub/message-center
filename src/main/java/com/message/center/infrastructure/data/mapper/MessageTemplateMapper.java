package com.message.center.infrastructure.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.message.center.domain.entity.MessageTemplate;
import com.message.center.domain.enums.ChannelType;

/**
 * 消息模板Mapper
 * 对应数据库表：msg_template
 */
public interface MessageTemplateMapper extends BaseMapper<MessageTemplate> {

    /**
     * 根据租户ID、业务类型和渠道类型查询消息模板
     * @param tenantId 租户ID
     * @param businessType 业务类型
     * @param channelType 渠道类型
     * @return 消息模板
     */
    default MessageTemplate selectByBusinessTypeAndChannelType(Long tenantId, String businessType, ChannelType channelType) {
        return selectOne(new LambdaQueryWrapper<MessageTemplate>()
                .eq(MessageTemplate::getTenantId, tenantId)
                .eq(MessageTemplate::getBusinessType, businessType)
                .eq(MessageTemplate::getChannelType, channelType));
    }

    /**
     * 查询启用的消息模板
     * @param tenantId 租户ID
     * @param businessType 业务类型
     * @param channelType 渠道类型
     * @return 消息模板
     */
    default MessageTemplate selectEnabledTemplate(Long tenantId, String businessType, ChannelType channelType) {
        return selectOne(new LambdaQueryWrapper<MessageTemplate>()
                .eq(MessageTemplate::getTenantId, tenantId)
                .eq(MessageTemplate::getBusinessType, businessType)
                .eq(MessageTemplate::getChannelType, channelType)
                .eq(MessageTemplate::getEnabled, true)
                .last("LIMIT 1"));
    }
}
