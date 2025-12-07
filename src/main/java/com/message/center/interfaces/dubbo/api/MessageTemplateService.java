package com.message.center.interfaces.dubbo.api;

import com.message.center.domain.entity.MessageTemplate;
import com.message.center.domain.enums.ChannelType;

/**
 * 消息模板服务
 */
public interface MessageTemplateService {
    /**
     * 根据ID获取消息模板
     * @param id 模板ID
     * @return 消息模板
     */
    MessageTemplate getById(Long id);

    /**
     * 保存消息模板
     * @param template 消息模板
     * @return 保存结果
     */
    boolean save(MessageTemplate template);

    /**
     * 更新消息模板
     * @param template 消息模板
     * @return 更新结果
     */
    boolean update(MessageTemplate template);

    /**
     * 删除消息模板
     * @param id 模板ID
     * @return 删除结果
     */
    boolean delete(Long id);

    /**
     * 根据条件查询消息模板
     * @param tenantId 租户ID
     * @param businessType 业务类型
     * @param channelType 渠道类型
     * @return 消息模板
     */
    MessageTemplate getTemplate(Long tenantId, String businessType, ChannelType channelType);

    /**
     * 渲染消息模板
     * @param template 消息模板
     * @param businessData 业务数据
     * @return 渲染后的消息内容
     */
    String renderTemplate(MessageTemplate template, String businessData);
}
