package org.wgtech.wgmall_backend.service;

import org.wgtech.wgmall_backend.entity.TaskLogger;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TaskLoggerService {

    /**
     * 发布一个“随机派单”任务（从用户余额内选一个商品）
     * @param userId 用户ID
     * @param username 用户名
     * @param userBalance 当前用户余额
     * @return 创建的任务对象（失败返回 null）
     */
    TaskLogger publishRandomTask(Long userId, String username, BigDecimal userBalance);
    /**
     * 发布一个“预留派单”任务（为用户预留任务，不强制下一个）
     * @param userId 用户ID
     * @param username 用户名
     * @param productId 商品ID
     * @param amount 金额
     * @param rebate 返利
     * @param dispatcher 发布人
     * @return 是否成功
     */
    boolean publishReservedTask(Long userId, String username, Long productId, BigDecimal amount, Double rebate, String dispatcher);

    /**
     * 获取该用户未领取的“预留任务”
     * @param userId 用户ID
     * @return Optional<TaskLogger>
     */
    Optional<TaskLogger> findUnTakenReservedTask(Long userId);

    List<TaskLogger> findUnTakenReservedTasks(Long userId); // ✅ 新增多任务接口

    public int countUnTakenReservedTasks(Long userId);

    int countUnCompletedReservedTasks(Long userId);

    /**
     * 保存任务日志（可用于更新领取/完成状态）
     * @param taskLogger 任务对象
     */
    void save(TaskLogger taskLogger);

    Optional<TaskLogger> findById(Long id);

    Optional<TaskLogger> findPendingTaskByUserId(Long userId);

    public List<TaskLogger> findCompletedTasksByUserId(Long userId);
}
