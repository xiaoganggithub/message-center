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
            channelTaskMapper.batchInsert(tasks);
            return true;
        } catch (Exception e) {
            log.error("批量保存渠道任务失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateStatus(String taskId, TaskStatus status, String result) {
        try {
            return channelTaskMapper.updateStatus(taskId, status, result) > 0;
        } catch (Exception e) {
            log.error("更新渠道任务状态失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<ChannelTask> getByMessageId(String messageId) {
        return channelTaskMapper.selectByMessageId(messageId);
    }
}