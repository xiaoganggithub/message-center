package com.message.center.domain.service;

import com.message.center.domain.entity.ChannelTask;
import com.message.center.domain.enums.TaskStatus;

import java.util.List;

/**
 * 渠道任务领域服务接口
 */
public interface ChannelTaskDomainService {

    /**
     * 创建渠道任务
     * @param channelTask 渠道任务对象
     * @return 创建后的渠道任务
     */
    ChannelTask createChannelTask(ChannelTask channelTask);

    /**
     * 批量创建渠道任务
     * @param channelTasks 渠道任务列表
     * @return 是否创建成功
     */
    boolean batchCreateChannelTasks(List<ChannelTask> channelTasks);

    /**
     * 更新渠道任务状态
     * @param taskId 任务ID
     * @param status 任务状态
     * @param result 执行结果
     * @return 是否更新成功
     */
    boolean updateChannelTaskStatus(String taskId, TaskStatus status, String result);

    /**
     * 根据消息ID获取渠道任务列表
     * @param messageId 消息ID
     * @return 渠道任务列表
     */
    List<ChannelTask> getChannelTasksByMessageId(String messageId);
}