package org.wgtech.wgmall_backend.service.impl;

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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
    public boolean publishReservedTask(Long userId, String username, Long productId,
                                       BigDecimal amount, Double rebate, String dispatcher, int triggerThreshold) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return false;

        TaskLogger task = TaskLogger.builder()
                .userId(userId)
                .username(username)
                .productId(productId)
                .productAmount(amount)
                .productName(product.getName())
                .productImagePath(product.getImagePath())
                .dispatchType(TaskLogger.DispatchType.RESERVED)
                .rebate(rebate)
                .dispatcher(dispatcher)
                .createTime(LocalDateTime.now())
                .triggerThreshold(triggerThreshold) // ✅ 设置触发条件
                .completed(false)
                .taken(false)
                .build();

        taskLoggerRepository.save(task);

        // 设置用户预约状态，仅在第一次时启用（可选）
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return false;

        if (!user.isAppointmentStatus()) {
            user.setAppointmentStatus(true);
        }

        userRepository.save(user);

        return true;
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


}
