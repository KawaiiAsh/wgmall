package org.wgtech.wgmall_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wgtech.wgmall_backend.entity.TaskLogger;

import java.util.Optional;

public interface TaskLoggerRepository extends JpaRepository<TaskLogger, Long> {

    /**
     * 判断用户是否存在未完成的任务
     * @param userId 用户ID
     * @return true 表示存在未完成任务
     */
    boolean existsByUserIdAndCompletedFalse(Long userId);

    /**
     * 查询该用户最新的一条未被领取的任务（指定类型）
     * @param userId 用户ID
     * @param dispatchType 派单类型（ASSIGNED / RESERVED）
     * @return Optional<TaskLogger>
     */
    Optional<TaskLogger> findFirstByUserIdAndDispatchTypeAndTakenFalseOrderByCreateTimeAsc(
            Long userId,
            TaskLogger.DispatchType dispatchType
    );
}
