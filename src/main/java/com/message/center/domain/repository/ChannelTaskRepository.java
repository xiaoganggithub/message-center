package com.message.center.domain.repository;

import com.message.center.domain.entity.ChannelTask;
import com.message.center.domain.enums.TaskStatus;

import java.util.List;

/**
 * 渠道任务仓库接口
 */
public interface ChannelTaskRepository {

    /**
     * 批量保存渠道任务
     * @param tasks 渠道任务列表
     * @return 是否保存成功
     */
    boolean batchSave(List<ChannelTask> tasks);

    /**
     * 批量保存渠道任务（别名方法）
     * @param tasks 渠道任务列表
     * @return 是否保存成功
     */
    boolean saveBatch(List<ChannelTask> tasks);

    /**
     * 更新渠道任务状态
     * @param taskId 任务ID
     * @param status 任务状态
     * @param result 执行结果
     * @return 是否更新成功
     */
    boolean updateStatus(String taskId, TaskStatus status, String result);

    /**
     * 根据消息ID查询渠道任务列表
     * @param messageId 消息ID
     * @return 渠道任务列表
     */
    List<ChannelTask> getByMessageId(String messageId);
}