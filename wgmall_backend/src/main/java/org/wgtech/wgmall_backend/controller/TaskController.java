package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.wgtech.wgmall_backend.dto.*;
import org.wgtech.wgmall_backend.entity.Product;
import org.wgtech.wgmall_backend.entity.TaskLogger;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.repository.TaskLoggerRepository;
import org.wgtech.wgmall_backend.repository.UserRepository;
import org.wgtech.wgmall_backend.service.GrabTaskService;
import org.wgtech.wgmall_backend.service.TaskLoggerService;
import org.wgtech.wgmall_backend.utils.Result;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

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

    @Autowired
    TaskLoggerRepository taskLoggerRepository;

    @PostMapping("/grab")
    @Operation(summary = "执行抢单（用户）")
    public Result<TaskResponse> grabTask(@RequestBody GrabTaskRequest request) {
        Long userId = request.getUserId();

        if (grabTaskService.hasComplete(userId)) {
            return Result.badRequest("你还有未完成的任务，请先完成后再抢单");
        }

        if (!grabTaskService.hasGrabPermission(userId)) {
            return Result.badRequest("抢单人数过多，过于繁忙");
        }

        if (grabTaskService.getRemainingGrabTimes(userId) <= 0) {
            return Result.badRequest("你的抢单数量不够");
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Result.tokenInvalid("用户不存在或登录失效");
        }

        Integer orderCount = user.getOrderCount() != null ? user.getOrderCount() : 0;

        TaskLogger task;

        // ✅ 优先查找匹配的预约任务
        Optional<TaskLogger> optionalTask = taskLoggerRepository
                .findFirstByUserIdAndDispatchTypeAndTakenFalseAndCompletedFalseAndTriggerThresholdOrderByTriggerThresholdAsc(
                        userId,
                        TaskLogger.DispatchType.RESERVED,
                        orderCount
                );

        if (optionalTask.isPresent()) {
            task = optionalTask.get();
            task.setTaken(true);

            if (taskLoggerService.countUnCompletedReservedTasks(userId) == 0) {
                user.setAppointmentNumber(null); // 可选：标记预约流程完成
            }

        } else {
            // ⛔ 未找到匹配预约任务，fallback 到随机派单
            task = taskLoggerService.publishRandomTask(userId, user.getUsername(), user.getBalance());
            if (task == null) {
                return Result.badRequest("暂无适合您余额的商品，无法派发任务");
            }
        }

        // ✅ 计算佣金和预期返还
        double rebateRate = (task.getDispatchType() == TaskLogger.DispatchType.RESERVED)
                ? task.getRebate()
                : user.getRebate();

        BigDecimal commission = task.getProductAmount().multiply(BigDecimal.valueOf(rebateRate));
        BigDecimal expectReturn = task.getProductAmount().add(commission);

        task.setCommission(commission);
        task.setExpectReturn(expectReturn);
        task.setTaken(true);

        user.setOrderCount(orderCount - 1);
//        user.setBalance(user.getBalance().subtract(task.getProductAmount()));

        userRepository.save(user);
        taskLoggerService.save(task);

        TaskResponse response = new TaskResponse(
                task.getId(),
                task.getProductImagePath(),
                task.getProductName(),
                task.getProductId(),
                task.getProductAmount(),
                task.getDispatchType(),
                task.getExpectReturn(),
                task.getCommission()
        );

        return Result.success(response);
    }





    @PostMapping("/complete")
    @Transactional
    public Result<?> completeTask(@RequestBody CompleteTaskRequest request) {
        System.out.println("🟢 /task/complete 接口调用，taskId = " + request.getTaskId());

        try {
            TaskLogger task = taskLoggerService.findById(request.getTaskId()).orElse(null);
            if (task == null) {
                System.out.println("🔴 任务不存在！");
                return Result.badRequest("任务不存在");
            }

            if (task.isCompleted()) {
                System.out.println("⚠️ 任务已完成，禁止重复提交");
                return Result.badRequest("任务已完成，不能重复提交");
            }

            User user = userRepository.findById(task.getUserId()).orElse(null);
            if (user == null) {
                System.out.println("🔴 用户不存在！");
                return Result.tokenInvalid("用户不存在或登录失效");
            }

            BigDecimal productAmount = task.getProductAmount();
            BigDecimal userBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;

            // ✅ 校验余额是否足够返还
            if (userBalance.compareTo(productAmount) < 0) {
                BigDecimal shortage = productAmount.subtract(userBalance).setScale(2, BigDecimal.ROUND_HALF_UP);
                InsufficientBalanceResponse response = new InsufficientBalanceResponse(
                        productAmount.setScale(2, BigDecimal.ROUND_HALF_UP),
                        userBalance.setScale(2, BigDecimal.ROUND_HALF_UP),
                        shortage,
                        task.getId()
                );
                return Result.custom(402, "余额不足", response); // ⚠️ 自定义 402 状态码用于余额不足提示
            }

            task.setCompleted(true);
            task.setCompleteTime(LocalDateTime.now());

            // ✅ 使用 expectReturn 和 commission 更新用户信息
            user.setBalance(userBalance.add(task.getCommission()));
            user.setTotalProfit(user.getTotalProfit().add(task.getCommission()));

            // ✅ 预约任务已完成清空标识
            if (task.getDispatchType() == TaskLogger.DispatchType.RESERVED &&
                    taskLoggerService.countUnCompletedReservedTasks(user.getId()) == 0) {
                user.setAppointmentNumber(null);
            }

            userRepository.save(user);
            taskLoggerService.save(task);

            System.out.println("✅ 任务完成成功");
            return Result.success("任务完成，返利：" + task.getCommission() + "，当前余额：" + user.getBalance());

        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure("任务完成失败，系统错误：" + e.getMessage());
        }
    }


//    @GetMapping("/debug")
//    public String debug(){
//        return "Debug";
//    }



    @PostMapping("/reserve")
    @Operation(summary = "管理员发布预约任务（一个任务，设置触发条件）")
    public Result<String> reserveTask(@RequestBody ReserveTaskRequest request) {
        boolean success = taskLoggerService.publishReservedTask(
                request.getUserId(),
                request.getUsername(),
                request.getProductId(),
                request.getProductAmount(),
                request.getCommissionRate(),
                request.getDispatcher(),
                request.getTriggerThreshold()  // ✅ 传入触发门槛
        );
        return success ? Result.success("预约任务发布成功") : Result.failure("预约任务发布失败");
    }


    @PostMapping("/pending")
    @Operation(summary = "查询当前用户未完成任务（购物车）（所有人）")
    public Result<List<TaskResponse>> getPendingTask(@RequestBody UserRequest request) {
        Optional<TaskLogger> optionalTask = taskLoggerService.findPendingTaskByUserId(request.getUserId());

        if (optionalTask.isEmpty()) {
            // ✅ 返回空数组
            return Result.success(Collections.emptyList());
        }

        TaskLogger task = optionalTask.get();

        TaskResponse response = new TaskResponse(
                task.getId(),
                task.getProductImagePath(),
                task.getProductName(),
                task.getProductId(),
                task.getProductAmount(),
                task.getDispatchType(),
                task.getCommission(),
                task.getExpectReturn()
        );

        return Result.success(List.of(response));
    }


    @PostMapping("/history")
    @Operation(summary = "查询当前用户已完成任务记录（分页，按完成时间倒序）（用户）")
    public Result<Map<String, Object>> getCompletedTasks(@RequestBody UserRequest request) {
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "completeTime"));

        Page<TaskLogger> completedTasksPage = taskLoggerService.findCompletedTasksByUserId(request.getUserId(), pageable);

        List<TaskResponse> responses = completedTasksPage.getContent().stream().map(task ->
                new TaskResponse(
                        task.getId(),
                        task.getProductImagePath(),
                        task.getProductName(),
                        task.getProductId(),
                        task.getProductAmount(),
                        task.getDispatchType(),
                        task.getCommission(),
                        task.getExpectReturn()
                )
        ).toList();

        Map<String, Object> result = new HashMap<>();
        result.put("content", responses);
        result.put("totalPages", completedTasksPage.getTotalPages());
        result.put("totalElements", completedTasksPage.getTotalElements());
        result.put("currentPage", page);
        result.put("last", completedTasksPage.isLast());

        return Result.success(result);
    }



}
