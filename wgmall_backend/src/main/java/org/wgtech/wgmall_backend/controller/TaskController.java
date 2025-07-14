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

    @PostMapping("/grab")
    @Operation(summary = "执行抢单")
    public Result<TaskResponse> grabTask(@RequestParam Long userId) {
        if (grabTaskService.hasComplete(userId)) {
            return Result.failure("你还有未完成的任务，请先完成后再抢单");
        }

        if (!grabTaskService.hasGrabPermission(userId)) {
            return Result.failure("抢单人数过多，过于繁忙");
        }

        if (grabTaskService.getRemainingGrabTimes(userId) <= 0) {
            return Result.failure("你的抢单数量不够");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        TaskLogger task = null;

        if (user.isAppointmentStatus()
                && user.getOrderCount() == user.getAppointmentNumber()) {

            List<TaskLogger> reservedTasks = taskLoggerService.findUnTakenReservedTasks(userId);
            if (reservedTasks.isEmpty()) {
                return Result.failure("暂无可领取的预约任务");
            }

            task = reservedTasks.get(0);
            task.setTaken(true);

            if (taskLoggerService.countUnCompletedReservedTasks(userId) == 0) {
                user.setAppointmentStatus(false);
            }

        } else {
            task = taskLoggerService.publishRandomTask(
                    user.getId(),
                    user.getUsername(),
                    user.getBalance()
            );

            if (task == null) {
                return Result.failure("暂无适合您余额的商品，无法派发任务");
            }
        }

        // 抢单成功后立即扣除金额（允许负数）
        user.setOrderCount(user.getOrderCount() - 1);
        user.setBalance(user.getBalance().subtract(task.getProductAmount()));
        userRepository.save(user);
        taskLoggerService.save(task);

        Product product = task.getProduct();

        TaskResponse response = new TaskResponse(
                task.getId(),
                product.getImagePath(),
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

        User user = userRepository.findById(task.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        task.setCompleted(true);
        task.setCompleteTime(LocalDateTime.now());
        taskLoggerService.save(task); // 先保存再判断状态

        double rebateRate = (task.getDispatchType() == TaskLogger.DispatchType.RESERVED)
                ? task.getRebate()
                : user.getRebate();

        BigDecimal rebateAmount = task.getProductAmount()
                .multiply(BigDecimal.valueOf(rebateRate));

        user.setBalance(user.getBalance().add(rebateAmount));
        user.setTotalProfit(user.getTotalProfit().add(rebateAmount));

        if (task.getDispatchType() == TaskLogger.DispatchType.RESERVED) {
            int remaining = taskLoggerService.countUnCompletedReservedTasks(user.getId());
            if (remaining == 0) {
                user.setAppointmentStatus(false);
            }
        }

        userRepository.save(user);

        return Result.success("任务完成，返利：" + rebateAmount + "，当前余额：" + user.getBalance());
    }

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
                product.getImagePath(),
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
                    product != null ? product.getImagePath() : null,
                    product != null ? product.getName() : null,
                    task.getProductId(),
                    task.getProductAmount(),
                    task.getDispatchType().name()
            );
        }).toList();

        return Result.success(responses);
    }
}
