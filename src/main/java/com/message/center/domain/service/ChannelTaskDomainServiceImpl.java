package com.message.center.domain.service.impl;

import com.message.center.domain.entity.ChannelTask;
import com.message.center.domain.enums.TaskStatus;
import com.message.center.domain.repository.ChannelTaskRepository;
import com.message.center.domain.service.ChannelTaskDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 渠道任务领域服务实现
 */
@Service
public class ChannelTaskDomainServiceImpl implements ChannelTaskDomainService {

    @Autowired
    private ChannelTaskRepository channelTaskRepository;

    @Override
    public ChannelTask createChannelTask(ChannelTask channelTask) {
        channelTask.setStatus(TaskStatus.PENDING);
        channelTaskRepository.batchSave(List.of(channelTask));
        return channelTask;
    }

    @Override
    public boolean batchCreateChannelTasks(List<ChannelTask> channelTasks) {
        channelTasks.forEach(task -> {
            if (task.getStatus() == null) {
                task.setStatus(TaskStatus.PENDING);
            }
        });
        return channelTaskRepository.batchSave(channelTasks);
    }

    @Override
    public boolean updateChannelTaskStatus(String taskId, TaskStatus status, String result) {
        return channelTaskRepository.updateStatus(taskId, status, result);
    }

    @Override
    public List<ChannelTask> getChannelTasksByMessageId(String messageId) {
        return channelTaskRepository.getByMessageId(messageId);
    }
}