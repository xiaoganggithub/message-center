package com.message.center.infrastructure.executor;

import com.message.center.infrastructure.adapter.ChannelAdapter;
import com.message.center.domain.entity.ChannelTask;
import com.message.center.domain.enums.ChannelType;
import com.message.center.domain.enums.TaskStatus;
import com.message.center.domain.vo.SendResult;
import com.message.center.domain.repository.ChannelTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 渠道任务执行器
 * 支持多渠道并发执行，实现故障隔离
 */
@Component
public class ChannelTaskExecutor {

    private static final Logger log = LoggerFactory.getLogger(ChannelTaskExecutor.class);

    @Autowired
    private Map<ChannelType, ChannelAdapter> adapterMap;

    @Autowired
    private ChannelTaskRepository taskRepository;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    /**
     * 并发执行所有渠道任务
     * 每个任务独立执行，互不影响
     * @param tasks 渠道任务列表
     */
    public void executeAll(List<ChannelTask> tasks) {
        // 使用CompletableFuture并发执行所有任务
        List<CompletableFuture<Void>> futures = tasks.stream()
                .map(task -> CompletableFuture.runAsync(() -> executeTask(task), taskExecutor))
                .toList();

        // 等待所有任务完成（可选择设置超时）
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .exceptionally(ex -> {
                    log.error("部分渠道任务执行失败", ex);
                    return null;
                });
    }

    /**
     * 执行单个渠道任务
     * 包含完整的错误处理和状态追踪
     * @param task 渠道任务
     */
    private void executeTask(ChannelTask task) {
        try {
            // 更新状态为发送中
            task.setStatus(TaskStatus.SENDING);
            taskRepository.updateStatus(task);

            // 获取对应的渠道适配器
            ChannelAdapter adapter = adapterMap.get(task.getChannelType());
            if (adapter == null) {
                throw new UnsupportedOperationException("不支持的渠道类型: " + task.getChannelType());
            }

            // 执行发送
            SendResult result = adapter.send(task);

            // 更新发送结果
            if (result.isSuccess()) {
                task.setStatus(TaskStatus.SUCCESS);
                task.setResultMessage("发送成功");
                task.setFinishTime(LocalDateTime.now());
            } else {
                handleFailure(task, result.getErrorMessage());
            }
        } catch (Exception e) {
            // 异常处理 - 不影响其他渠道
            handleFailure(task, e.getMessage());
        } finally {
            // 更新任务
            task.setUpdateTime(LocalDateTime.now());
            taskRepository.updateStatus(task);
        }
    }

    /**
     * 处理发送失败
     * @param task 渠道任务
     * @param errorMessage 错误信息
     */
    private void handleFailure(ChannelTask task, String errorMessage) {
        task.setResultMessage(errorMessage);

        // 判断是否需要重试
        if (task.getRetryCount() < task.getMaxRetry()) {
            task.setStatus(TaskStatus.RETRY);
            task.setRetryCount(task.getRetryCount() + 1);
            // 计算下次重试时间（指数退避）
            int delay = (int) Math.pow(2, task.getRetryCount()) * 60;
            task.setNextRetryTime(LocalDateTime.now().plusSeconds(delay));
        } else {
            task.setStatus(TaskStatus.FAILED);
            task.setFinishTime(LocalDateTime.now());
        }
    }
}
