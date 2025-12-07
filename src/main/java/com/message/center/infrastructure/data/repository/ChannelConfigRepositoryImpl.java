package com.message.center.infrastructure.data.repository;

import com.message.center.domain.entity.ChannelConfig;
import com.message.center.domain.enums.ChannelType;
import com.message.center.domain.repository.ChannelConfigRepository;
import com.message.center.infrastructure.data.mapper.ChannelConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
        return channelConfigMapper.selectByTenantStoreAndType(tenantId, storeId, channelType);
    }
}