package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.repository.UserRepository;
import org.wgtech.wgmall_backend.service.UserService;
import org.wgtech.wgmall_backend.utils.Result;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

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

    @Autowired
    private UserRepository userRepository;

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

    /**
     * 获取当前登录用户的信息
     *
     * @return 用户信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取指定用户信息（开发用）", description = "开发阶段通过 userId 查询用户信息")
    public Result<User> getUserInfo(@RequestParam Long userId) {
        return userService.getUserInfoById(userId);
    }

    /**
     * 根据用户id设置返点
     * @param userId
     * @param rebate
     * @return
     */
    @PutMapping("/set-rbate")
    @Operation(summary = "设置用户返点", description = "根据用户ID设置用户返点")
    public Result<String> setUserRebate(@RequestParam Long userId,
                                        @RequestParam double rebate) {

        try {
            userService.setRebate(userId, rebate);
            return Result.success("返点设置成功为：" + rebate);
        } catch (IllegalArgumentException e) {
            return Result.failure(e.getMessage());
        }
    }

    @GetMapping("/profit")
    @Operation(summary = "获取用户盈利统计")
    public Result<Map<String, Object>> getProfit(@RequestParam Long userId) {
        BigDecimal today = userService.getTodayProfit(userId);
        BigDecimal yesterday = userService.getYesterdayProfit(userId);
        BigDecimal total = userRepository.findById(userId).get().getTotalProfit();


        Map<String, Object> result = new HashMap<>();
        result.put("today", today);
        result.put("yesterday", yesterday);
        result.put("total", total);

        return Result.success(result);
    }

    @PostMapping("/role/set")
    @Operation(summary = "设置用户的买家或者卖家")
    public Result<String> setBuyerOrSaler(@RequestParam Long userId,
                                          @RequestParam int buyerOrSaler) {
        int result = userService.setBuyerOrSaler(userId, buyerOrSaler);

        String roleName;
        switch (result) {
            case 1: roleName = "买家"; break;
            case 2: roleName = "卖家"; break;
            default: roleName = "未知角色"; break;
        }

        return Result.success("成功设置为：" + roleName);
    }

    @GetMapping
    @Operation(summary = "分页查询用户", description = "按注册时间倒序，默认每页10条")
    public Result<Page<User>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        // 限制最大每页100条，避免滥用
        int safeSize = Math.min(pageSize, 100);
        Page<User> users = userService.getAllUsersSortedByCreateTime(page, safeSize);
        return Result.success(users);
    }

    @PutMapping("/tron/set")
    @Operation(summary = "设置 Tron 地址")
    public Result<Void> setTron(@RequestParam Long userId, @RequestParam String address) {
        userService.setTronAddress(userId, address);
        return Result.success();
    }

    @GetMapping("/tron/get")
    @Operation(summary = "获取 Tron 地址")
    public Result<String> getTron(@RequestParam Long userId) {
        return Result.success(userService.getTronAddress(userId));
    }

    @PutMapping("/btc/set")
    @Operation(summary = "设置 BTC 地址")
    public Result<Void> setBtc(@RequestParam Long userId, @RequestParam String address) {
        userService.setBtcAddress(userId, address);
        return Result.success();
    }

    @GetMapping("/btc/get")
    @Operation(summary = "获取 BTC 地址")
    public Result<String> getBtc(@RequestParam Long userId) {
        return Result.success(userService.getBtcAddress(userId));
    }

    @PutMapping("/eth/set")
    @Operation(summary = "设置 ETH 地址")
    public Result<Void> setEth(@RequestParam Long userId, @RequestParam String address) {
        userService.setEthAddress(userId, address);
        return Result.success();
    }

    @GetMapping("/eth/get")
    @Operation(summary = "获取 ETH 地址")
    public Result<String> getEth(@RequestParam Long userId) {
        return Result.success(userService.getEthAddress(userId));
    }

    @PutMapping("/coin/set")
    @Operation(summary = "设置 Coin 地址")
    public Result<Void> setCoin(@RequestParam Long userId, @RequestParam String address) {
        userService.setCoinAddress(userId, address);
        return Result.success();
    }

    @GetMapping("/coin/get")
    @Operation(summary = "获取 Coin 地址")
    public Result<String> getCoin(@RequestParam Long userId) {
        return Result.success(userService.getCoinAddress(userId));
    }
}
