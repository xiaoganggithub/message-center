package com.message.center.infrastructure.data.repository;

import com.message.center.domain.entity.ChannelTask;
import com.message.center.domain.enums.TaskStatus;
import com.message.center.domain.repository.ChannelTaskRepository;
import com.message.center.infrastructure.data.mapper.ChannelTaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 渠道任务仓库实现
 */
@Repository
@Slf4j
public class ChannelTaskRepositoryImpl implements ChannelTaskRepository {

    @Autowired
    private ChannelTaskMapper channelTaskMapper;

    @Override
    public boolean batchSave(List<ChannelTask> tasks) {
        try {
            for (ChannelTask task : tasks) {
                channelTaskMapper.insert(task);
            }
            return true;
        } catch (Exception e) {
            log.error("批量保存渠道任务失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean saveBatch(List<ChannelTask> tasks) {
        return batchSave(tasks);
    }

    @Override
    public boolean updateStatus(String taskId, TaskStatus status, String result) {
        try {
            ChannelTask task = channelTaskMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ChannelTask>()
                            .eq(ChannelTask::getId, Long.parseLong(taskId))
            );
            if (task != null) {
                task.setStatus(status);
                task.setResultMessage(result);
                task.setUpdateTime(java.time.LocalDateTime.now());
                if (status == TaskStatus.SUCCESS || status == TaskStatus.FAILED) {
                    task.setFinishTime(java.time.LocalDateTime.now());
                }
                return channelTaskMapper.updateById(task) > 0;
            }
            return false;
        } catch (Exception e) {
            log.error("更新渠道任务状态失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<ChannelTask> getByMessageId(String messageId) {
        return channelTaskMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ChannelTask>()
                        .eq(ChannelTask::getMessageId, messageId)
        );
    }
}