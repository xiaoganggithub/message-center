package com.message.center.infrastructure.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.message.center.domain.entity.ChannelConfig;
import com.message.center.domain.enums.ChannelType;

import java.util.List;

/**
 * 渠道配置Mapper
 * 对应数据库表：msg_channel_config
 */
public interface ChannelConfigMapper extends BaseMapper<ChannelConfig> {

    /**
     * 根据租户ID和门店ID查询渠道配置
     * @param tenantId 租户ID
     * @param storeId 门店ID
     * @param businessType 业务类型
     * @return 渠道配置列表
     */
    default List<ChannelConfig> selectByTenantAndStore(Long tenantId, Long storeId, String businessType) {
        LambdaQueryWrapper<ChannelConfig> queryWrapper = new LambdaQueryWrapper<ChannelConfig>()
                .eq(ChannelConfig::getTenantId, tenantId);

        if (storeId != null) {
            queryWrapper.and(wrapper -> wrapper
                    .eq(ChannelConfig::getStoreId, storeId)
                    .or().isNull(ChannelConfig::getStoreId));
        } else {
            queryWrapper.isNull(ChannelConfig::getStoreId);
        }

        queryWrapper.orderByAsc(ChannelConfig::getPriority);
        return selectList(queryWrapper);
    }

    /**
     * 根据渠道类型查询渠道配置
     * @param tenantId 租户ID
     * @param storeId 门店ID
     * @param channelType 渠道类型
     * @return 渠道配置
     */
    default ChannelConfig selectByChannelType(Long tenantId, Long storeId, ChannelType channelType) {
        LambdaQueryWrapper<ChannelConfig> queryWrapper = new LambdaQueryWrapper<ChannelConfig>()
                .eq(ChannelConfig::getTenantId, tenantId)
                .eq(ChannelConfig::getChannelType, channelType)
                .eq(ChannelConfig::getEnabled, true);

        if (storeId != null) {
            queryWrapper.and(wrapper -> wrapper
                    .eq(ChannelConfig::getStoreId, storeId)
                    .or().isNull(ChannelConfig::getStoreId));
        } else {
            queryWrapper.isNull(ChannelConfig::getStoreId);
        }

        queryWrapper.orderByAsc(ChannelConfig::getPriority)
                .last("LIMIT 1");
        return selectOne(queryWrapper);
    }

    /**
     * 查询启用的渠道配置
     * @param tenantId 租户ID
     * @param storeId 门店ID
     * @return 渠道配置列表
     */
    default List<ChannelConfig> selectEnabledConfigs(Long tenantId, Long storeId) {
        LambdaQueryWrapper<ChannelConfig> queryWrapper = new LambdaQueryWrapper<ChannelConfig>()
                .eq(ChannelConfig::getTenantId, tenantId)
                .eq(ChannelConfig::getEnabled, true);

        if (storeId != null) {
            queryWrapper.and(wrapper -> wrapper
                    .eq(ChannelConfig::getStoreId, storeId)
                    .or().isNull(ChannelConfig::getStoreId));
        } else {
            queryWrapper.isNull(ChannelConfig::getStoreId);
        }

        queryWrapper.orderByAsc(ChannelConfig::getPriority);
        return selectList(queryWrapper);
    }
}
