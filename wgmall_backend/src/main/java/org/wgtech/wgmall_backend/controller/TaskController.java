package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wgtech.wgmall_backend.dto.TaskResponse;
import org.wgtech.wgmall_backend.entity.TaskLogger;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.repository.UserRepository;
import org.wgtech.wgmall_backend.service.GrabTaskService;
import org.wgtech.wgmall_backend.service.TaskLoggerService;
import org.wgtech.wgmall_backend.utils.Result;

import java.math.BigDecimal;

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

        if (user.isAssignedStatus()) {
            // 指定派单
            task = taskLoggerService.findUnTakenAssignedTask(userId)
                    .orElse(null);
            if (task == null) {
                return Result.failure("未找到分配给你的指定任务");
            }
            task.setTaken(true);
            user.setAssignedStatus(false);

        } else if (user.isAppointmentStatus()
                && user.getOrderCount() == user.getAppointmentNumber()) {
            // 预约派单
            task = taskLoggerService.findUnTakenReservedTask(userId)
                    .orElse(null);
            if (task == null) {
                return Result.failure("未找到预约派单任务");
            }
            task.setTaken(true);
            user.setAppointmentStatus(false);

        } else {
            // 随机派单
            task = taskLoggerService.publishRandomTask(
                    user.getId(),
                    user.getUsername(),
                    user.getRebate(),
                    BigDecimal.valueOf(user.getBalance())
            );
            if (task == null) {
                return Result.failure("暂无适合您余额的商品，无法派发任务");
            }
        }

        // 统一操作：扣减次数、保存
        user.setOrderCount(user.getOrderCount() - 1);
        userRepository.save(user);
        taskLoggerService.save(task); // 别忘了保存更新后的task

        // 构建 TaskResponse 对象
        TaskResponse response = new TaskResponse(
                task.getId(),
                task.getProductId(),
                task.getProductAmount(),
                task.getDispatchType().name(),
                true // 默认都需要付款
        );

        return Result.success(response);
    }


    @PostMapping("/assign")
    @Operation(summary = "管理员发布指定派单任务")
    public Result<String> assignTask(
            @RequestParam Long userId,
            @RequestParam String username,
            @RequestParam Long productId,
            @RequestParam BigDecimal productAmount,
            @RequestParam Double commissionRate,
            @RequestParam String dispatcher  // 👈 新增
    ) {
        boolean success = taskLoggerService.publishAssignedTask(
                userId, username, productId, productAmount, commissionRate, dispatcher
        );
        return success ? Result.success("指定任务发布成功") : Result.failure("指定任务发布失败");
    }

    @PostMapping("/reserve")
    @Operation(summary = "管理员发布预约派单任务")
    public Result<String> reserveTask(
            @RequestParam Long userId,
            @RequestParam String username,
            @RequestParam Long productId,
            @RequestParam BigDecimal productAmount,
            @RequestParam Double commissionRate,
            @RequestParam String dispatcher  // 👈 新增
    ) {
        boolean success = taskLoggerService.publishReservedTask(
                userId, username, productId, productAmount, commissionRate, dispatcher
        );
        return success ? Result.success("预约任务发布成功") : Result.failure("预约任务发布失败");
    }



}
