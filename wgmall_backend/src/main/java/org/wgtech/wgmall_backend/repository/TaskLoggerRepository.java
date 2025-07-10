package org.wgtech.wgmall_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wgtech.wgmall_backend.entity.TaskLogger;

import java.util.Optional;

public interface TaskLoggerRepository extends JpaRepository<TaskLogger, Long> {

    // 判断是否存在未完成任务
    boolean existsByUserIdAndCompletedFalse(Long userId);

    Optional<TaskLogger> findFirstByUserIdAndDispatchTypeAndTakenFalseOrderByCreateTimeAsc(Long userId, TaskLogger.DispatchType dispatchType);

}

