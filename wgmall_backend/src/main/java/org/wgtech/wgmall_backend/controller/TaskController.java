package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wgtech.wgmall_backend.dto.TaskResponse;
import org.wgtech.wgmall_backend.entity.Product;
import org.wgtech.wgmall_backend.entity.TaskLogger;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.repository.UserRepository;
import org.wgtech.wgmall_backend.service.GrabTaskService;
import org.wgtech.wgmall_backend.service.TaskLoggerService;
import org.wgtech.wgmall_backend.utils.Result;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务控制器 - 实现刷单流程中涉及的任务分发与领取
 */
@RestController
@RequestMapping("/task")
@Tag(name = "刷单流程接口", description = "实现刷单所需要的接口")
public class TaskController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    GrabTaskService grabTaskService;

    @Autowired
    TaskLoggerService taskLoggerService;

    /**
     * 用户执行抢单（仅支持预约派单 + 随机派单）
     */
    @PostMapping("/grab")
    @Operation(summary = "执行抢单")
    public Result<TaskResponse> grabTask(@RequestParam Long userId) {

        // 1. 判断是否还有未完成的任务
        if (grabTaskService.hasComplete(userId)) {
            return Result.failure("你还有未完成的任务，请先完成后再抢单");
        }

        // 2. 检查是否允许继续抢单（如并发抢单限制）
        if (!grabTaskService.hasGrabPermission(userId)) {
            return Result.failure("抢单人数过多，过于繁忙");
        }

        // 3. 检查用户剩余抢单次数
        if (grabTaskService.getRemainingGrabTimes(userId) <= 0) {
            return Result.failure("你的抢单数量不够");
        }

        // 4. 查询用户信息
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        TaskLogger task = null;

        // 5. 优先预约派单（支持多个预约任务）
        if (user.isAppointmentStatus()
                && user.getOrderCount() == user.getAppointmentNumber()) {

            List<TaskLogger> reservedTasks = taskLoggerService.findUnTakenReservedTasks(userId);

            task = reservedTasks.get(0); // 默认领取最早的一个
            task.setTaken(true);

            // ✅ 如果所有预约任务都已完成，清除预约状态
            if (taskLoggerService.countUnCompletedReservedTasks(userId) == 0) {
                user.setAppointmentStatus(false);
            }

        } else {
            // 6. 随机派单
            task = taskLoggerService.publishRandomTask(
                    user.getId(),
                    user.getUsername(),
                    BigDecimal.valueOf(user.getBalance())
            );

            if (task == null) {
                return Result.failure("暂无适合您余额的商品，无法派发任务");
            }
        }

        // 7. 更新用户状态与任务状态
        user.setOrderCount(user.getOrderCount() - 1);
        userRepository.save(user);
        taskLoggerService.save(task);

        Product product = task.getProduct();

        // 8. 构造并返回响应结果
        TaskResponse response = new TaskResponse(
                task.getId(),
                product.getFirstImagePath(),
                product.getName(),
                task.getProductId(),
                task.getProductAmount(),
                task.getDispatchType().name()
        );


        return Result.success(response);
    }

    @PostMapping("/complete")
    @Operation(summary = "完成任务按钮（加返利）")
    public Result<String> completeTask(@RequestParam Long taskId) {
        TaskLogger task = taskLoggerService.findById(taskId)
                .orElse(null);

        if (task == null) {
            return Result.failure("任务不存在");
        }

        if (Boolean.TRUE.equals(task.getCompleted())) {
            return Result.failure("任务已完成，不能重复提交");
        }

        // 获取用户（已确认用户一定存在）
        User user = userRepository.findById(task.getUserId()).get();

        // 标记任务为完成
        task.setCompleted(true);
        task.setCompleteTime(LocalDateTime.now()); //  记录完成时间

        // ✅ 根据任务类型选择返利比例
        double rebateRate;
        if (task.getDispatchType() == TaskLogger.DispatchType.RESERVED) {
            rebateRate = task.getRebate(); // 来自业务员设定
        } else {
            rebateRate = user.getRebate(); // 用户自己的返利比例
        }

        // 计算返利金额：商品金额 × 返利比例
        BigDecimal rebateAmount = task.getProductAmount()
                .multiply(BigDecimal.valueOf(rebateRate));

        // 累加用户总盈利
        user.setTotalProfit(user.getTotalProfit() + rebateAmount.doubleValue());

        // 可选：发放到账户余额
         user.setBalance(user.getBalance() + rebateAmount.doubleValue());

        // 保存更新
        taskLoggerService.save(task);
        userRepository.save(user);

        return Result.success("任务已完成，返利：" + rebateAmount);
    }


    /**
     * 管理员发布“预约派单”任务
     * 用户满足预约条件（如数量）后才可抢
     */
    @PostMapping("/reserve")
    @Operation(summary = "管理员发布预约派单任务")
    public Result<String> reserveTask(
            @RequestParam Long userId,
            @RequestParam String username,
            @RequestParam Long productId,
            @RequestParam BigDecimal productAmount,
            @RequestParam Double commissionRate,
            @RequestParam String dispatcher
    ) {
        boolean success = taskLoggerService.publishReservedTask(
                userId, username, productId, productAmount, commissionRate, dispatcher
        );
        return success ? Result.success("预约任务发布成功") : Result.failure("预约任务发布失败");
    }

    @GetMapping("/pending")
    @Operation(summary = "查询当前用户未完成任务（购物车）")
    public Result<TaskResponse> getPendingTask(@RequestParam Long userId) {
        TaskLogger task = taskLoggerService.findPendingTaskByUserId(userId)
                .orElse(null);

        if (task == null) {
            return Result.failure("你没有未完成的任务");
        }

        Product product = task.getProduct();

        TaskResponse response = new TaskResponse(
                task.getId(),
                product.getFirstImagePath(),
                product.getName(),
                task.getProductId(),
                task.getProductAmount(),
                task.getDispatchType().name()
        );

        return Result.success(response);
    }

    @GetMapping("/history")
    @Operation(summary = "查询当前用户已完成任务记录（历史）")
    public Result<List<TaskResponse>> getCompletedTasks(@RequestParam Long userId) {
        List<TaskLogger> completedTasks = taskLoggerService.findCompletedTasksByUserId(userId);

        if (completedTasks == null || completedTasks.isEmpty()) {
            return Result.failure("你还没有完成的任务记录");
        }

        List<TaskResponse> responses = completedTasks.stream().map(task -> {
            Product product = task.getProduct();

            return new TaskResponse(
                    task.getId(),
                    product != null ? product.getFirstImagePath() : null,
                    product != null ? product.getName() : null,
                    task.getProductId(),
                    task.getProductAmount(),
                    task.getDispatchType().name()
            );
        }).toList();

        return Result.success(responses);
    }



}
