package org.wgtech.wgmall_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.wgtech.wgmall_backend.entity.TaskLogger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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


    // ✅ 新增：查询所有未领取的预约派单任务
    List<TaskLogger> findByUserIdAndDispatchTypeAndTakenFalseOrderByCreateTimeAsc(
            Long userId,
            TaskLogger.DispatchType dispatchType
    );

    int countByUserIdAndDispatchTypeAndTakenFalse(Long userId, TaskLogger.DispatchType dispatchType);

    int countByUserIdAndDispatchTypeAndCompletedFalse(Long userId, TaskLogger.DispatchType dispatchType);

    List<TaskLogger> findByUserIdAndDispatchTypeAndTakenFalseAndCompletedFalseOrderByCreateTimeAsc(
            Long userId,
            TaskLogger.DispatchType dispatchType
    );

    @Query("SELECT COALESCE(SUM(t.productAmount * " +
            "CASE WHEN t.dispatchType = 'RESERVED' THEN t.rebate ELSE u.rebate END), 0) " +
            "FROM TaskLogger t JOIN User u ON t.userId = u.id " +
            "WHERE t.userId = :userId AND t.completed = true " +
            "AND t.completeTime BETWEEN :start AND :end")
    BigDecimal calculateProfitBetween(@Param("userId") Long userId,
                                      @Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);


    Optional<TaskLogger> findFirstByUserIdAndTakenTrueAndCompletedFalseOrderByCreateTimeAsc(Long userId);

    List<TaskLogger> findByUserIdAndCompletedTrueOrderByCompleteTimeDesc(Long userId);


}
