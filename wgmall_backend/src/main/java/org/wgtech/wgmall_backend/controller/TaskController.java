package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wgtech.wgmall_backend.dto.TaskResponse;
import org.wgtech.wgmall_backend.entity.TaskLogger;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.repository.UserRepository;
import org.wgtech.wgmall_backend.service.GrabTaskService;
import org.wgtech.wgmall_backend.service.TaskLoggerService;
import org.wgtech.wgmall_backend.utils.Result;

import java.math.BigDecimal;

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
     * 用户执行抢单
     * 处理逻辑：
     * 1. 检查是否有未完成任务
     * 2. 判断系统是否允许抢单（并发控制）
     * 3. 检查用户是否还有抢单次数
     * 4. 根据用户状态分派任务：
     *    - 指定派单
     *    - 预约派单
     *    - 随机派单（匹配用户余额）
     * 5. 更新用户抢单次数和任务状态
     * 6. 返回任务响应结果
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

        // 5.1 指定派单：用户被管理员明确派单
        if (user.isAssignedStatus()) {
            task = taskLoggerService.findUnTakenAssignedTask(userId).orElse(null);
            if (task == null) {
                return Result.failure("未找到分配给你的指定任务");
            }
            task.setTaken(true); // 标记任务为已领取
            user.setAssignedStatus(false); // 清除指定状态

            // 5.2 预约派单：用户预约了任务并达到数量要求
        } else if (user.isAppointmentStatus()
                && user.getOrderCount() == user.getAppointmentNumber()) {
            task = taskLoggerService.findUnTakenReservedTask(userId).orElse(null);
            if (task == null) {
                return Result.failure("未找到预约派单任务");
            }
            task.setTaken(true);
            user.setAppointmentStatus(false); // 清除预约状态

            // 5.3 随机派单：默认模式，根据用户余额分配合适的商品
        } else {
            task = taskLoggerService.publishRandomTask(
                    user.getId(),
                    user.getUsername(),
                    BigDecimal.valueOf(user.getBalance()) // 用余额匹配商品金额
            );
            if (task == null) {
                return Result.failure("暂无适合您余额的商品，无法派发任务");
            }
        }

        // 6. 更新抢单次数和保存状态
        user.setOrderCount(user.getOrderCount() - 1);
        userRepository.save(user);     // 更新用户
        taskLoggerService.save(task);  // 更新任务状态

        // 7. 构造响应返回
        TaskResponse response = new TaskResponse(
                task.getId(),
                task.getProductId(),
                task.getProductAmount(),
                task.getDispatchType().name(), // ASSIGNED / RESERVED / RANDOM
                true // 所有任务默认都需付款
        );

        return Result.success(response);
    }

    /**
     * 管理员发布“指定派单”任务
     * 该任务只派发给特定用户
     */
    @PostMapping("/assign")
    @Operation(summary = "管理员发布指定派单任务")
    public Result<String> assignTask(
            @RequestParam Long userId,
            @RequestParam String username,
            @RequestParam Long productId,
            @RequestParam BigDecimal productAmount,
            @RequestParam Double commissionRate,
            @RequestParam String dispatcher // 操作人（管理员名）
    ) {
        boolean success = taskLoggerService.publishAssignedTask(
                userId, username, productId, productAmount, commissionRate, dispatcher
        );
        return success ? Result.success("指定任务发布成功") : Result.failure("指定任务发布失败");
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
}
