package com.message.center.infrastructure.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.annotation.Insert;
import com.baomidou.mybatisplus.annotation.Update;
import com.message.center.domain.entity.ChannelTask;
import com.message.center.domain.enums.TaskStatus;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 渠道任务Mapper
 * 对应数据库表：msg_channel_task
 */
public interface ChannelTaskMapper extends BaseMapper<ChannelTask> {

    /**
     * 根据消息ID查询渠道任务
     * @param messageId 消息ID
     * @return 渠道任务列表
     */
    default List<ChannelTask> selectByMessageId(String messageId) {
        return selectList(new LambdaQueryWrapper<ChannelTask>()
                .eq(ChannelTask::getMessageId, messageId));
    }

    /**
     * 更新任务状态
     * @param id 任务ID
     * @param status 任务状态
     * @return 更新结果
     */
    default int updateStatus(Long id, TaskStatus status) {
        return update(null, new LambdaUpdateWrapper<ChannelTask>()
                .eq(ChannelTask::getId, id)
                .set(ChannelTask::getStatus, status));
    }

    /**
     * 查询待重试任务
     * @param currentTime 当前时间
     * @return 待重试任务列表
     */
    default List<ChannelTask> selectRetryTasks(LocalDateTime currentTime) {
        return selectList(new LambdaQueryWrapper<ChannelTask>()
                .eq(ChannelTask::getStatus, TaskStatus.RETRY)
                .le(ChannelTask::getNextRetryTime, currentTime));
    }

    /**
     * 批量插入渠道任务
     * @param tasks 渠道任务列表
     * @return 插入结果
     */
    default int batchInsert(List<ChannelTask> tasks) {
        int count = 0;
        for (ChannelTask task : tasks) {
            if (insert(task) > 0) {
                count++;
            }
        }
        return count;
    }
}
