package org.wgtech.wgmall_backend.service.impl;
import lombok.extern.slf4j.Slf4j;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.wgtech.wgmall_backend.entity.Product;
import org.wgtech.wgmall_backend.entity.TaskLogger;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.repository.ProductRepository;
import org.wgtech.wgmall_backend.repository.TaskLoggerRepository;
import org.wgtech.wgmall_backend.repository.UserRepository;
import org.wgtech.wgmall_backend.service.TaskLoggerService;
import org.wgtech.wgmall_backend.utils.Result;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskLoggerServiceImpl implements TaskLoggerService {

    private final ProductRepository productRepository;
    private final TaskLoggerRepository taskLoggerRepository;
    private final UserRepository userRepository;

    /**
     * 为用户发布一个“随机”任务（从可支付范围内的商品中随机选一个）
     * @param userId 用户ID
     * @param username 用户名
     * @param userBalance 用户当前余额
     * @return 创建的任务对象（可能为 null）
     */
    @Override
    public TaskLogger publishRandomTask(Long userId, String username, BigDecimal userBalance) {
        // 1. 查找用户可支付的商品列表
        List<Product> productList = productRepository.findByPriceLessThanEqual(userBalance);
        if (productList.isEmpty()) return null;

        // 2. 从中随机选择一个商品
        Product selected = productList.get(new Random().nextInt(productList.size()));

        // 3. 获取用户返利比例
        Double rebate = userRepository.findById(userId)
                .map(User::getRebate)
                .orElse(0.0);

        // 4. 构建任务对象（类型为“随机”）
        TaskLogger task = TaskLogger.builder()
                .userId(userId)
                .username(username)
                .productId(selected.getId())               // ✅ 商品ID快照
                .productName(selected.getName())           // ✅ 商品名称快照
                .productAmount(selected.getPrice())        // ✅ 商品价格快照
                .productImagePath(selected.getImagePath()) // ✅ 商品图片快照
                .dispatchType(TaskLogger.DispatchType.RANDOM)
                .rebate(rebate)
                .dispatcher("随机订单")
                .createTime(LocalDateTime.now())
                .completed(false)
                .taken(false)
                .build();

        // 5. 保存任务记录
        return taskLoggerRepository.save(task);
    }


    /**
     * 发布一个“预留”类型的任务（通常是为特定用户准备的）
     */
    @Override
    public Result<String> publishReservedTask(Long userId, String username, Long productId,
                                              BigDecimal amount, Double rebate, String dispatcher, int triggerThreshold) {
        log.info("🟡 开始发布预约任务：userId={}, username={}, productId={}, amount={}, rebate={}, dispatcher={}, triggerThreshold={}",
                userId, username, productId, amount, rebate, dispatcher, triggerThreshold);

        try {
            // 1. 校验商品是否存在
            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) {
                log.warn("🔴 发布失败：未找到对应商品，productId={}", productId);
                return Result.failure("商品不存在");
            }

            // ✅ 2.1 校验是否已有相同触发条件的未完成任务
            Optional<TaskLogger> existingTaskOpt = taskLoggerRepository
                    .findFirstByUserIdAndDispatchTypeAndTriggerThresholdAndCompletedFalse(
                            userId, TaskLogger.DispatchType.RESERVED, triggerThreshold);

            if (existingTaskOpt.isPresent()) {
                log.warn("🟠 已存在相同触发条件的未完成预约任务，userId={}, triggerThreshold={}", userId, triggerThreshold);
                return Result.failure("您不能为同一个人连续派两次触发次数一样的单，请等待他先完成");
            }

            // 计算佣金与预期返还
            BigDecimal commission = amount.multiply(BigDecimal.valueOf(rebate));
            BigDecimal expectReturn = amount.add(commission);

            // 构建任务实体
            TaskLogger task = TaskLogger.builder()
                    .userId(userId)
                    .username(username)
                    .productId(productId)
                    .productAmount(amount)
                    .productName(product.getName())
                    .productImagePath(product.getImagePath())
                    .dispatchType(TaskLogger.DispatchType.RESERVED)
                    .rebate(rebate)
                    .commission(commission)
                    .expectReturn(expectReturn)
                    .dispatcher(dispatcher)
                    .createTime(LocalDateTime.now())
                    .triggerThreshold(triggerThreshold)
                    .completed(false)
                    .taken(false)
                    .build();
            // 3. 保存任务
            taskLoggerRepository.save(task);
            log.info("✅ 预约任务保存成功，taskId={}", task.getId());

            // 4. 设置用户预约状态
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                log.warn("🔴 发布失败：未找到对应用户，userId={}", userId);
                return Result.failure("用户不存在");
            }

            if (!user.isAppointmentStatus()) {
                user.setAppointmentStatus(true);
                userRepository.save(user);
                log.info("🟢 用户预约状态已更新为 true，userId={}", userId);
            }

            return Result.success("预约任务发布成功");

        } catch (Exception e) {
            log.error("🛑 发布预约任务过程中出现异常", e);
            return Result.failure("服务器内部错误，请稍后再试");
        }
    }





    /**
     * 查询用户未领取的“预留任务”
     */
    @Override
    public Optional<TaskLogger> findUnTakenReservedTask(Long userId) {
        return taskLoggerRepository.findFirstByUserIdAndDispatchTypeAndTakenFalseOrderByCreateTimeAsc(
                userId, TaskLogger.DispatchType.RESERVED
        );
    }

    @Override
    public List<TaskLogger> findUnTakenReservedTasks(Long userId) {
        return taskLoggerRepository.findByUserIdAndDispatchTypeAndTakenFalseAndCompletedFalseOrderByCreateTimeAsc(
                userId,
                TaskLogger.DispatchType.RESERVED
        );
    }

    @Override
    public int countUnTakenReservedTasks(Long userId) {
        return taskLoggerRepository.countByUserIdAndDispatchTypeAndTakenFalse(
                userId,
                TaskLogger.DispatchType.RESERVED
        );
    }

    @Override
    public int countUnCompletedReservedTasks(Long userId) {
        return taskLoggerRepository.countByUserIdAndDispatchTypeAndCompletedFalse(
                userId,
                TaskLogger.DispatchType.RESERVED
        );
    }

    @Override
    public Optional<TaskLogger> findById(Long id) {
        return taskLoggerRepository.findById(id);
    }

    /**
     * 保存任务（用于更新状态如领取、完成等）
     */
    @Override
    public void save(TaskLogger taskLogger) {
        taskLoggerRepository.saveAndFlush(taskLogger);
    }


    @Override
    public Optional<TaskLogger> findPendingTaskByUserId(Long userId) {
        return taskLoggerRepository.findFirstByUserIdAndTakenTrueAndCompletedFalseOrderByCreateTimeAsc(userId);
    }

    @Override
    public Page<TaskLogger> findCompletedTasksByUserId(Long userId, Pageable pageable) {
        return taskLoggerRepository.findByUserIdAndCompletedTrue(userId, pageable);
    }

    @Override
    public Page<TaskLogger> findAllTasks(Pageable pageable) {
        return taskLoggerRepository.findAll(pageable);
    }

    @Override
    public Page<TaskLogger> findByDispatchType(TaskLogger.DispatchType dispatchType, Pageable pageable) {
        return taskLoggerRepository.findByDispatchType(dispatchType, pageable);
    }

    @Override
    public Page<TaskLogger> findCompletedTasksByUsername(String username, Pageable pageable) {
        return taskLoggerRepository.findByUsernameAndCompletedTrueOrderByCompleteTimeDesc(username, pageable);
    }

    @Override
    public Page<TaskLogger> findTasksByUsername(String username, Pageable pageable) {
        return taskLoggerRepository.findByUsernameOrderByCompleteTimeDesc(username, pageable);
    }

}
