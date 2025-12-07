package com.message.center.interfaces.rest;

import com.message.center.domain.entity.ChannelConfig;
import com.message.center.service.api.ChannelConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 渠道配置控制器
 * 处理渠道配置的CRUD操作
 */
@RestController
@RequestMapping("/api/channel-configs")
public class ChannelConfigController {

    @Autowired
    private ChannelConfigService channelConfigService;

    /**
     * 创建渠道配置
     * @param config 渠道配置
     * @return 创建结果
     */
    @PostMapping
    public ResponseEntity<MessageController.ApiResponse<ChannelConfig>> createChannelConfig(@RequestBody ChannelConfig config) {
        try {
            boolean result = channelConfigService.save(config);
            if (result) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new MessageController.ApiResponse<>(HttpStatus.CREATED.value(), "创建成功", config));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new MessageController.ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "创建失败", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageController.ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "创建失败: " + e.getMessage(), null));
        }
    }

    /**
     * 更新渠道配置
     * @param id 配置ID
     * @param config 渠道配置
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<MessageController.ApiResponse<ChannelConfig>> updateChannelConfig(@PathVariable Long id, @RequestBody ChannelConfig config) {
        try {
            config.setId(id);
            boolean result = channelConfigService.update(config);
            if (result) {
                return ResponseEntity.ok(new MessageController.ApiResponse<>(HttpStatus.OK.value(), "更新成功", config));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new MessageController.ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "更新失败", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageController.ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "更新失败: " + e.getMessage(), null));
        }
    }

    /**
     * 删除渠道配置
     * @param id 配置ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageController.ApiResponse<Boolean>> deleteChannelConfig(@PathVariable Long id) {
        try {
            boolean result = channelConfigService.delete(id);
            if (result) {
                return ResponseEntity.ok(new MessageController.ApiResponse<>(HttpStatus.OK.value(), "删除成功", true));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new MessageController.ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "删除失败", false));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageController.ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "删除失败: " + e.getMessage(), false));
        }
    }

    /**
     * 查询渠道配置详情
     * @param id 配置ID
     * @return 渠道配置
     */
    @GetMapping("/{id}")
    public ResponseEntity<MessageController.ApiResponse<ChannelConfig>> getChannelConfig(@PathVariable Long id) {
        try {
            ChannelConfig config = channelConfigService.getById(id);
            if (config != null) {
                return ResponseEntity.ok(new MessageController.ApiResponse<>(HttpStatus.OK.value(), "success", config));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageController.ApiResponse<>(HttpStatus.NOT_FOUND.value(), "渠道配置不存在", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageController.ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "查询失败: " + e.getMessage(), null));
        }
    }

    /**
     * 查询渠道配置列表
     * @param tenantId 租户ID
     * @param storeId 门店ID
     * @param businessType 业务类型
     * @return 渠道配置列表
     */
    @GetMapping
    public ResponseEntity<MessageController.ApiResponse<List<ChannelConfig>>> getChannelConfigs(@RequestParam Long tenantId,
                                                                                              @RequestParam(required = false) Long storeId,
                                                                                              @RequestParam(required = false) String businessType) {
        try {
            List<ChannelConfig> configs = channelConfigService.getConfigs(tenantId, storeId, businessType);
            return ResponseEntity.ok(new MessageController.ApiResponse<>(HttpStatus.OK.value(), "success", configs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageController.ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "查询失败: " + e.getMessage(), null));
        }
    }
}
