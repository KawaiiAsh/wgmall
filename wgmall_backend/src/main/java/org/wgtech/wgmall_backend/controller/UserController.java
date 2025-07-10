package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.service.UserService;
import org.wgtech.wgmall_backend.utils.Result;

/**
 * 用户操作控制器
 * 包含：加钱、扣钱、设置抢单资格与次数
 */
@RestController
@RequestMapping("/user")
@Tag(name = "用户接口", description = "用于操作用户数据的接口")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 给用户账户加钱
     *
     * @param userId 用户 ID
     * @param amount 金额（正数）
     * @return 修改后的用户信息或失败信息
     */
    @PostMapping("/add-money")
    @Operation(summary = "给用户加钱", description = "根据用户ID增加余额")
    public Result<User> addMoney(
            @RequestParam Long userId,
            @RequestParam double amount
    ) {
        return userService.addMoney(userId, amount);
    }

    /**
     * 从用户账户扣除金额
     *
     * @param userId 用户 ID
     * @param amount 扣除金额（正数）
     * @return 修改后的用户信息或失败提示
     */
    @PostMapping("/minus-money")
    @Operation(summary = "扣除用户余额", description = "根据用户ID扣除余额")
    public Result<User> minusMoney(
            @RequestParam Long userId,
            @RequestParam double amount
    ) {
        return userService.minusMoney(userId, amount);
    }

    /**
     * 设置用户是否有资格抢单
     *
     * @param userId 用户 ID
     * @param eligible 是否可抢单（true=可以，false=禁止）
     * @return 更新后的用户信息
     */
    @PostMapping("/set-grab-eligibility")
    @Operation(summary = "设置用户抢单资格", description = "根据用户ID设置能否抢单")
    public Result<User> setGrabEligibility(
            @RequestParam Long userId,
            @RequestParam boolean eligible
    ) {
        return userService.setGrabOrderEligibility(userId, eligible);
    }

    /**
     * 设置用户剩余可抢单次数
     *
     * @param userId 用户 ID
     * @param times 剩余次数（整数）
     * @return 修改后的用户信息
     */
    @PostMapping("/set-grab-times")
    @Operation(summary = "设置用户抢单次数", description = "根据用户ID设置剩余抢单次数")
    public Result<User> setGrabTimes(
            @RequestParam Long userId,
            @RequestParam int times
    ) {
        return userService.setGrabOrderTimes(userId, times);
    }
}
