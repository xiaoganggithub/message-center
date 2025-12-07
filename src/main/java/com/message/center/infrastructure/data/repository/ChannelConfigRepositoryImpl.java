package com.message.center.infrastructure.data.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.message.center.domain.entity.ChannelConfig;
import com.message.center.domain.enums.ChannelType;
import com.message.center.domain.repository.ChannelConfigRepository;
import com.message.center.infrastructure.data.mapper.ChannelConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 渠道配置仓库实现
 */
@Repository
@Slf4j
public class ChannelConfigRepositoryImpl implements ChannelConfigRepository {

    @Autowired
    private ChannelConfigMapper channelConfigMapper;

    @Override
    public ChannelConfig getByTenantStoreAndType(Long tenantId, Long storeId, ChannelType channelType) {
        return channelConfigMapper.selectOne(new LambdaQueryWrapper<ChannelConfig>()
                .eq(ChannelConfig::getTenantId, tenantId)
                .eq(ChannelConfig::getStoreId, storeId)
                .eq(ChannelConfig::getChannelType, channelType));
    }

    @Override
    public ChannelConfig getById(Long id) {
        return channelConfigMapper.selectById(id);
    }

    @Override
    public List<ChannelConfig> getEnabledConfigs(Long tenantId, Long storeId) {
        return channelConfigMapper.selectList(new LambdaQueryWrapper<ChannelConfig>()
                .eq(ChannelConfig::getTenantId, tenantId)
                .eq(ChannelConfig::getStoreId, storeId)
                .eq(ChannelConfig::getEnabled, true));
    }

    @Override
    public List<ChannelConfig> getConfigs(Long tenantId, Long storeId, String businessType) {
        return channelConfigMapper.selectList(new LambdaQueryWrapper<ChannelConfig>()
                .eq(ChannelConfig::getTenantId, tenantId)
                .eq(ChannelConfig::getStoreId, storeId)
                .eq(ChannelConfig::getTenantId, tenantId)
                .eq(ChannelConfig::getStoreId, storeId));
    }
}