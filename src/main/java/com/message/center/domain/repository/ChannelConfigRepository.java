package com.message.center.domain.repository;

import com.message.center.domain.entity.ChannelConfig;
import com.message.center.domain.enums.ChannelType;

import java.util.List;

/**
 * 渠道配置仓库接口
 */
public interface ChannelConfigRepository {

    /**
     * 根据租户ID、门店ID和渠道类型查询渠道配置
     * @param tenantId 租户ID
     * @param storeId 门店ID
     * @param channelType 渠道类型
     * @return 渠道配置对象
     */
    ChannelConfig getByTenantStoreAndType(Long tenantId, Long storeId, ChannelType channelType);

    /**
     * 根据ID查询渠道配置
     * @param id 配置ID
     * @return 渠道配置对象
     */
    ChannelConfig getById(Long id);

    /**
     * 查询启用的渠道配置
     * @param tenantId 租户ID
     * @param storeId 门店ID
     * @return 渠道配置列表
     */
    List<ChannelConfig> getEnabledConfigs(Long tenantId, Long storeId);

    /**
     * 根据租户ID、门店ID和业务类型查询渠道配置
     * @param tenantId 租户ID
     * @param storeId 门店ID
     * @param businessType 业务类型
     * @return 渠道配置列表
     */
    List<ChannelConfig> getConfigs(Long tenantId, Long storeId, String businessType);
}