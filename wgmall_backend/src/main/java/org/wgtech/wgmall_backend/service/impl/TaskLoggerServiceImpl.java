package org.wgtech.wgmall_backend.service.impl;

import lombok.RequiredArgsConstructor;
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
     * 发布随机任务
     * @param userId 用户ID
     * @param username 用户名
     * @param rebate 返点
     * @param userBalance 用户余额
     * @return
     */
    @Override
    public TaskLogger publishRandomTask(Long userId, String username, Double rebate,BigDecimal userBalance) {
        List<Product> productList = productRepository.findByPriceLessThanEqual(userBalance);
        if (productList.isEmpty()) return null;

        Product selected = productList.get(new Random().nextInt(productList.size()));

        Double userCommission = userRepository.findById(userId)
                .map(User::getRebate)
                .orElse(0.0);

        TaskLogger task = TaskLogger.builder()
                .userId(userId)
                .username(username)
                .productId(selected.getId())
                .productAmount(selected.getPrice())
                .dispatchType(TaskLogger.DispatchType.RANDOM)
                .rebate(rebate)
                .dispatcher("随机订单")
                .createTime(LocalDateTime.now())
                .completed(false)
                .taken(false)
                .build();

        return taskLoggerRepository.save(task);
    }

    @Override
    public boolean publishAssignedTask(Long userId, String username, Long productId, BigDecimal amount, Double rebate, String dispatcher) {
        TaskLogger task = TaskLogger.builder()
                .userId(userId)
                .username(username)
                .productId(productId)
                .productAmount(amount)
                .dispatchType(TaskLogger.DispatchType.ASSIGNED)
                .rebate(rebate)
                .dispatcher(dispatcher)  // 👈 设置发布人
                .createTime(LocalDateTime.now())
                .completed(false)
                .taken(false)
                .build();
        taskLoggerRepository.save(task);
        return true;
    }


    @Override
    public boolean publishReservedTask(Long userId, String username, Long productId, BigDecimal amount, Double rebate, String dispatcher) {
        TaskLogger task = TaskLogger.builder()
                .userId(userId)
                .username(username)
                .productId(productId)
                .productAmount(amount)
                .dispatchType(TaskLogger.DispatchType.RESERVED)
                .rebate(rebate)
                .dispatcher(dispatcher)  // 👈 设置发布人
                .createTime(LocalDateTime.now())
                .completed(false)
                .taken(false)
                .build();
        taskLoggerRepository.save(task);
        return true;
    }

    @Override
    public Optional<TaskLogger> findUnTakenAssignedTask(Long userId) {
        return taskLoggerRepository.findFirstByUserIdAndDispatchTypeAndTakenFalseOrderByCreateTimeAsc(
                userId, TaskLogger.DispatchType.ASSIGNED
        );
    }

    @Override
    public Optional<TaskLogger> findUnTakenReservedTask(Long userId) {
        return taskLoggerRepository.findFirstByUserIdAndDispatchTypeAndTakenFalseOrderByCreateTimeAsc(
                userId, TaskLogger.DispatchType.RESERVED
        );
    }

    @Override
    public void save(TaskLogger taskLogger) {
        taskLoggerRepository.save(taskLogger);
    }
}
