package com.message.center.interfaces.rest;

import com.message.center.domain.entity.MessageTemplate;
import com.message.center.interfaces.dubbo.api.MessageTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 消息模板控制器
 * 处理消息模板的CRUD操作
 */
@RestController
@RequestMapping("/api/templates")
public class MessageTemplateController {

    @Autowired
    private MessageTemplateService messageTemplateService;

    /**
     * 创建消息模板
     * @param template 消息模板
     * @return 创建结果
     */
    @PostMapping
    public ResponseEntity<MessageController.ApiResponse<MessageTemplate>> createTemplate(@RequestBody MessageTemplate template) {
        try {
            boolean result = messageTemplateService.save(template);
            if (result) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new MessageController.ApiResponse<>(HttpStatus.CREATED.value(), "创建成功", template));
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
     * 更新消息模板
     * @param id 模板ID
     * @param template 消息模板
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<MessageController.ApiResponse<MessageTemplate>> updateTemplate(@PathVariable Long id, @RequestBody MessageTemplate template) {
        try {
            template.setId(id);
            boolean result = messageTemplateService.update(template);
            if (result) {
                return ResponseEntity.ok(new MessageController.ApiResponse<>(HttpStatus.OK.value(), "更新成功", template));
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
     * 删除消息模板
     * @param id 模板ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageController.ApiResponse<Boolean>> deleteTemplate(@PathVariable Long id) {
        try {
            boolean result = messageTemplateService.delete(id);
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
     * 查询消息模板详情
     * @param id 模板ID
     * @return 消息模板
     */
    @GetMapping("/{id}")
    public ResponseEntity<MessageController.ApiResponse<MessageTemplate>> getTemplate(@PathVariable Long id) {
        try {
            MessageTemplate template = messageTemplateService.getById(id);
            if (template != null) {
                return ResponseEntity.ok(new MessageController.ApiResponse<>(HttpStatus.OK.value(), "success", template));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageController.ApiResponse<>(HttpStatus.NOT_FOUND.value(), "消息模板不存在", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageController.ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "查询失败: " + e.getMessage(), null));
        }
    }
}
