package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.wgtech.wgmall_backend.repository.UserRepository;
import org.wgtech.wgmall_backend.service.GrabTaskService;
import org.wgtech.wgmall_backend.service.TaskLoggerService;
import org.wgtech.wgmall_backend.utils.Result;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Operation(summary = "执行抢单（用户）")
    public Result<TaskResponse> grabTask(@RequestBody GrabTaskRequest request) {
        Long userId = request.getUserId();
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

        TaskLogger task;

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

        user.setOrderCount(user.getOrderCount() - 1);
        user.setBalance(user.getBalance().subtract(task.getProductAmount()));
        userRepository.save(user);
        taskLoggerService.save(task);

        Product product = task.getProduct();

        if (product == null) {
            throw new RuntimeException("任务的商品信息为空！Task ID: " + task.getId());
        }
        
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
    @Operation(summary = "完成任务按钮（加返利）（用户）")
    public Result<String> completeTask(@RequestBody CompleteTaskRequest request) {
        TaskLogger task = taskLoggerService.findById(request.getTaskId())
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
        taskLoggerService.save(task);

        double rebateRate = (task.getDispatchType() == TaskLogger.DispatchType.RESERVED)
                ? task.getRebate()
                : user.getRebate();

        BigDecimal rebateAmount = task.getProductAmount()
                .multiply(BigDecimal.valueOf(rebateRate));

        user.setBalance(user.getBalance().add(rebateAmount));
        user.setTotalProfit(user.getTotalProfit().add(rebateAmount));

        if (task.getDispatchType() == TaskLogger.DispatchType.RESERVED) {
            if (taskLoggerService.countUnCompletedReservedTasks(user.getId()) == 0) {
                user.setAppointmentStatus(false);
            }
        }

        userRepository.save(user);

        return Result.success("任务完成，返利：" + rebateAmount + "，当前余额：" + user.getBalance());
    }

    @PostMapping("/reserve")
    @Operation(summary = "管理员发布预约派单任务（身份“SALES，BOSS“）的权限")
    public Result<String> reserveTask(@RequestBody ReserveTaskRequest request) {
        boolean success = taskLoggerService.publishReservedTask(
                request.getUserId(),
                request.getUsername(),
                request.getProductId(),
                request.getProductAmount(),
                request.getCommissionRate(),
                request.getDispatcher()
        );
        return success ? Result.success("预约任务发布成功") : Result.failure("预约任务发布失败");
    }

    @PostMapping("/pending")
    @Operation(summary = "查询当前用户未完成任务（购物车）（所有人）")
    public Result<TaskResponse> getPendingTask(@RequestBody UserRequest request) {
        TaskLogger task = taskLoggerService.findPendingTaskByUserId(request.getUserId())
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

    @PostMapping("/history")
    @Operation(summary = "查询当前用户已完成任务记录（分页，按完成时间倒序）（用户）")
    public Result<Map<String, Object>> getCompletedTasks(
            @RequestBody UserRequest request,
            @RequestParam(defaultValue = "0") int page
    ) {
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "completeTime"));

        Page<TaskLogger> completedTasksPage = taskLoggerService.findCompletedTasksByUserId(request.getUserId(), pageable);

        if (completedTasksPage.isEmpty()) {
            return Result.failure("你还没有完成的任务记录");
        }

        List<TaskResponse> responses = completedTasksPage.getContent().stream().map(task -> {
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

        // 返回分页结构
        Map<String, Object> result = new HashMap<>();
        result.put("content", responses);
        result.put("totalPages", completedTasksPage.getTotalPages());
        result.put("totalElements", completedTasksPage.getTotalElements());
        result.put("currentPage", page);
        result.put("last", completedTasksPage.isLast());

        return Result.success(result);
    }


}
