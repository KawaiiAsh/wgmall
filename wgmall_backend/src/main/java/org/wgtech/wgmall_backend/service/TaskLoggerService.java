package org.wgtech.wgmall_backend.service;

import org.wgtech.wgmall_backend.entity.TaskLogger;

import java.math.BigDecimal;
import java.util.Optional;

public interface TaskLoggerService {

    /**
     * 发布一个随机任务（金额 ≤ 用户余额），返回是否发布成功
     * @param userId 用户ID
     * @param username 用户名
     * @param userBalance 用户余额
     * @return 是否成功
     */
    TaskLogger publishRandomTask(Long userId, String username, Double rebate,BigDecimal userBalance);

    /**
     * 用户的下一个必定是我们的指定任务
     * @param userId
     * @param username
     * @param productId
     * @param amount
     * @param rebate
     * @return
     */
    boolean publishAssignedTask(Long userId, String username, Long productId, BigDecimal amount, Double rebate, String dispatcher);

    /**
     * 用户的
     * @param userId
     * @param username
     * @param productId
     * @param amount
     * @param rebate
     * @return
     */
    boolean publishReservedTask(Long userId, String username, Long productId, BigDecimal amount, Double rebate, String dispatcher);

    Optional<TaskLogger> findUnTakenAssignedTask(Long userId);

    Optional<TaskLogger> findUnTakenReservedTask(Long userId);

    void save(TaskLogger taskLogger);
}
