package com.message.center.interfaces.dubbo.api;

import com.message.center.domain.entity.ChannelConfig;
import com.message.center.domain.enums.ChannelType;

import java.util.List;

/**
 * 渠道配置服务
 */
public interface ChannelConfigService {
    /**
     * 根据ID获取渠道配置
     * @param id 配置ID
     * @return 渠道配置
     */
    ChannelConfig getById(Long id);

    /**
     * 保存渠道配置
     * @param config 渠道配置
     * @return 保存结果
     */
    boolean save(ChannelConfig config);

    /**
     * 更新渠道配置
     * @param config 渠道配置
     * @return 更新结果
     */
    boolean update(ChannelConfig config);

    /**
     * 删除渠道配置
     * @param id 配置ID
     * @return 删除结果
     */
    boolean delete(Long id);

    /**
     * 根据条件查询渠道配置
     * @param tenantId 租户ID
     * @param storeId 门店ID
     * @param businessType 业务类型
     * @return 渠道配置列表
     */
    List<ChannelConfig> getConfigs(Long tenantId, Long storeId, String businessType);

    /**
     * 获取渠道的频次限制配置
     * @param tenantId 租户ID
     * @param storeId 门店ID
     * @param channelType 渠道类型
     * @return 频次限制配置
     */
    RateLimitConfig getRateLimitConfig(Long tenantId, Long storeId, ChannelType channelType);

    /**
     * 频次限制配置
     */
    class RateLimitConfig {
        /** 限制数量 */
        private Integer count;
        /** 时间窗口大小 */
        private Integer window;
        /** 时间单位：SECOND/MINUTE/HOUR/DAY */
        private String unit;

        // 构造方法
        public RateLimitConfig(Integer count, Integer window, String unit) {
            this.count = count;
            this.window = window;
            this.unit = unit;
        }

        // getter和setter方法
        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Integer getWindow() {
            return window;
        }

        public void setWindow(Integer window) {
            this.window = window;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }
    }
}
