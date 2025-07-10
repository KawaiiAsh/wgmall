package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.service.UserService;
import org.wgtech.wgmall_backend.utils.Result;

@RestController
@RequestMapping("/user")
@Tag(name = "用户接口", description = "用于操作用户数据的接口")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/add-money")
    @Operation(summary = "给用户加钱", description = "根据用户ID增加余额")
    public Result<User> addMoney(
            @RequestParam Long userId,
            @RequestParam double amount
    ) {
        return userService.addMoney(userId, amount);
    }

    @PostMapping("/minus-money")
    @Operation(summary = "扣除用户余额", description = "根据用户ID扣除余额")
    public Result<User> minusMoney(
            @RequestParam Long userId,
            @RequestParam double amount
    ) {
        return userService.minusMoney(userId, amount);
    }

    @PostMapping("/set-grab-eligibility")
    @Operation(summary = "设置用户抢单资格", description = "根据用户ID设置能否抢单")
    public Result<User> setGrabEligibility(
            @RequestParam Long userId,
            @RequestParam boolean eligible
    ) {
        return userService.setGrabOrderEligibility(userId, eligible);
    }

    @PostMapping("/set-grab-times")
    @Operation(summary = "设置用户抢单次数", description = "根据用户ID设置剩余抢单次数")
    public Result<User> setGrabTimes(
            @RequestParam Long userId,
            @RequestParam int times
    ) {
        return userService.setGrabOrderTimes(userId, times);
    }
}
