package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.wgtech.wgmall_backend.dto.*;
import org.wgtech.wgmall_backend.entity.RechargeRecord;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.entity.WithdrawalRecord;
import org.wgtech.wgmall_backend.repository.RechargeRecordRepository;
import org.wgtech.wgmall_backend.repository.UserRepository;
import org.wgtech.wgmall_backend.repository.WithdrawalRecordRepository;
import org.wgtech.wgmall_backend.service.UserService;
import org.wgtech.wgmall_backend.utils.Result;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户操作控制器
 * 包含：加钱、扣钱、设置抢单资格与次数
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Tag(name = "用户接口", description = "用于操作用户数据的接口")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RechargeRecordRepository rechargeRecordRepository;

    @Autowired
    private WithdrawalRecordRepository withdrawalRecordRepository;
    /**
     * 给用户账户加钱
     *
     * @return 修改后的用户信息或失败信息
     */

    @PostMapping("/add-money")
    @Operation(summary = "给用户加钱（身份“SALES，BOSS“的权限）")
    public Result<User> addMoney(@RequestBody AddMoneyRequest request) {
        return userService.addMoney(request.getUserId(), request.getAmount());
    }

    /**
     * 从用户账户扣除金额
     *
     * @return 修改后的用户信息或失败提示
     */
    @PostMapping("/minus-money")
    @Operation(summary = "扣除用户余额（身份“SALES，BOSS“的权限）")
    public Result<User> minusMoney(@RequestBody MinusMoneyRequest request) {
        return userService.minusMoney(request.getUserId(), request.getAmount());
    }

    @PostMapping("/set-grab-toggle")
    @Operation(summary = "设置用户抢单开关（身份“SALES，BOSS“的权限）")
    public Result<User> setGrabToggle(@RequestBody GrabToggleRequest request) {
        try {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

            user.setToggle(request.isToggle());
            userRepository.save(user);

            return Result.success(user);
        } catch (Exception e) {
            return Result.failure("设置抢单开关失败：" + e.getMessage());
        }
    }


    @PostMapping("/set-grab-times")
    @Operation(summary = "设置用户抢单次数（身份“SALES，BOSS“的权限）")
    public Result<User> setGrabTimes(@RequestBody GrabTimesRequest request) {
        return userService.setGrabOrderTimes(request.getUserId(), request.getTimes());
    }

    /**
     * 获取当前登录用户的信息
     *
     * @return 用户信息
     */
    @GetMapping("/info/{userId}")
    @Operation(summary = "获取指定用户信息")
    public Result<User> getUserInfo(@PathVariable Long userId) {
        return userService.getUserInfoById(userId);
    }


    /**
     * 根据用户id设置返点
     * @return
     */
    @PutMapping("/set-rbate")
    @Operation(summary = "设置用户返点（身份“SALES，BOSS“的权限）", description = "根据用户ID设置用户返点")
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
    @Operation(summary = "获取用户盈利统计（身份“BUYER，SALER“的权限）")
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
    @Operation(summary = "设置用户的买家或者卖家（身份“SALES，BOSS“的权限）")
    public Result<String> setBuyerOrSaler(@RequestBody SetRoleRequest request) {
        int result = userService.setBuyerOrSaler(request.getUserId(), request.getBuyerOrSaler());
        String roleName = switch (result) {
            case 1 -> "买家";
            case 2 -> "卖家";
            default -> "未知角色";
        };
        return Result.success("成功设置为：" + roleName);
    }

    @GetMapping
    @Operation(summary = "分页查询用户（身份“SALES，BOSS“的权限）", description = "按注册时间倒序，默认每页10条")
    public Result<Page<User>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        // 限制最大每页100条，避免滥用
        int safeSize = Math.min(pageSize, 100);
        Page<User> users = userService.getAllUsersSortedByCreateTime(page, safeSize);
        return Result.success(users);
    }

    @GetMapping("/total-order-count")
    @Operation(summary = "获取用户总刷单次数（所有人）", description = "通过用户ID查询该用户的总刷单次数")
    public Result<Integer> getTotalOrderCount(@RequestParam Long userId) {
        try {
            // Fetching user by userId
            User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("用户未找到"));

            // Returning the total order count of the user
            return Result.success(user.getTotalOrderCount());
        } catch (Exception e) {
            // Handling errors
            return Result.failure("查询失败: " + e.getMessage());
        }
    }

    @PutMapping("/tron/set")
    @Operation(summary = "设置 Tron 地址（用户）")
    public Result<Void> setTron(@RequestParam Long userId, @RequestParam String address) {
        userService.setTronAddress(userId, address);
        return Result.success();
    }

    @GetMapping("/tron/get")
    @Operation(summary = "获取 Tron 地址（所有人）")
    public Result<String> getTron(@RequestParam Long userId) {
        return Result.success(userService.getTronAddress(userId));
    }

    @PutMapping("/btc/set")
    @Operation(summary = "设置 BTC 地址（用户）")
    public Result<Void> setBtc(@RequestParam Long userId, @RequestParam String address) {
        userService.setBtcAddress(userId, address);
        return Result.success();
    }

    @GetMapping("/btc/get")
    @Operation(summary = "获取 BTC 地址（所有人）")
    public Result<String> getBtc(@RequestParam Long userId) {
        return Result.success(userService.getBtcAddress(userId));
    }

    @PutMapping("/eth/set")
    @Operation(summary = "设置 ETH 地址（用户）")
    public Result<Void> setEth(@RequestParam Long userId, @RequestParam String address) {
        userService.setEthAddress(userId, address);
        return Result.success();
    }

    @GetMapping("/eth/get")
    @Operation(summary = "获取 ETH 地址（所有人）")
    public Result<String> getEth(@RequestParam Long userId) {
        return Result.success(userService.getEthAddress(userId));
    }

    @PutMapping("/coin/set")
    @Operation(summary = "设置 Coin 地址（用户）")
    public Result<Void> setCoin(@RequestParam Long userId, @RequestParam String address) {
        userService.setCoinAddress(userId, address);
        return Result.success();
    }

    @GetMapping("/coin/get")
    @Operation(summary = "获取 Coin 地址（所有人）")
    public Result<String> getCoin(@RequestParam Long userId) {
        return Result.success(userService.getCoinAddress(userId));
    }

    @GetMapping("/balance")
    @Operation(summary = "获取用户余额（身份“SALES，BOSS“）的权限", description = "根据用户ID获取余额")
    public Result<BigDecimal> getUserBalance(@RequestParam Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("用户未找到"));
            return Result.success(user.getBalance());
        } catch (Exception e) {
            return Result.failure("查询失败: " + e.getMessage());
        }
    }

    @GetMapping("/user-detail/{userId}")
    @Operation(summary = "获取指定用户的详细信息（包含基本信息和盈利统计）")
    public Result<Map<String, Object>> getUserDetail(@PathVariable Long userId) {
        try {
            log.info("获取用户详情，userId = {}", userId);

            Result<User> userInfoResult = userService.getUserInfoById(userId);
            if (!userInfoResult.isSuccess() || userInfoResult.getData() == null) {
                log.warn("未找到用户，userId = {}", userId);
                return Result.failure("用户信息获取失败");
            }

            BigDecimal today = userService.getTodayProfit(userId);
            BigDecimal yesterday = userService.getYesterdayProfit(userId);
            BigDecimal total = userRepository.findById(userId)
                    .map(User::getTotalProfit)
                    .orElse(BigDecimal.ZERO);

            Map<String, Object> result = new HashMap<>();
            result.put("user", userInfoResult.getData());

            // ✅ 改用 HashMap，避免 null 报错
            Map<String, Object> profit = new HashMap<>();
            profit.put("today", today == null ? BigDecimal.ZERO : today);
            profit.put("yesterday", yesterday == null ? BigDecimal.ZERO : yesterday);
            profit.put("total", total == null ? BigDecimal.ZERO : total);
            result.put("profit", profit);

            return Result.success(result);
        } catch (Exception e) {
            log.error("获取用户详情出错 userId = {}", userId, e);
            return Result.failure("服务器异常：" + e.getMessage());
        }
    }


    @GetMapping("/withdraw/check")
    @Operation(summary = "检查用户是否允许提现（所有人）", description = "根据用户 canWithdraw 字段判断是否允许提现")
    public Result<String> checkWithdrawPermission(@RequestParam Long userId) {
        // 查询用户
        User user = userRepository.findById(userId)
                .orElse(null);

        if (user == null) {
            return Result.notFound("用户不存在");
        }

        // 判断提现权限
        if (!user.isCanWithdraw()) {
            // code = 500，根据你提供的 failure(message) 定义
            return Result.failure("您还要未完成的订单");
        }

        // 可以提现，提示前端跳转
        return Result.success("允许提现，请跳转至客服页面");
    }


    @GetMapping("/withdrawals/{userId}")
    @Operation(summary = "根据用户ID获取提现记录", description = "返回指定用户的所有提现记录，仅包含金额和提现日期，按时间倒序")
    public Result<List<Map<String, Object>>> getWithdrawalRecordsByUserId(@PathVariable Long userId) {
        List<WithdrawalRecord> records = withdrawalRecordRepository.findByUserIdOrderByWithdrawalTimeDesc(userId);

        List<Map<String, Object>> resultList = records.stream().map(record -> {
            Map<String, Object> map = new HashMap<>();
            map.put("amount", record.getAmount());
            map.put("withdrawalDate", new SimpleDateFormat("yyyy-MM-dd").format(record.getWithdrawalTime()));
            return map;
        }).collect(Collectors.toList());

        return Result.success(resultList);
    }

    @GetMapping("/withdrawals/approved/{userId}")
    @Operation(summary = "用户查询已通过的提现记录", description = "仅返回已审核通过的记录，按时间倒序")
    public Result<List<Map<String, Object>>> getApprovedWithdrawalsByUser(@PathVariable Long userId) {
        List<WithdrawalRecord> records = withdrawalRecordRepository.findByUserIdAndStatusOrderByWithdrawalTimeDesc(userId, "APPROVED");

        List<Map<String, Object>> resultList = records.stream().map(record -> {
            Map<String, Object> map = new HashMap<>();
            map.put("amount", record.getAmount());
            map.put("withdrawalDate", new SimpleDateFormat("yyyy-MM-dd").format(record.getWithdrawalTime()));
            return map;
        }).collect(Collectors.toList());

        return Result.success(resultList);
    }



    @PostMapping("/withdraw")
    @Operation(summary = "提交提现申请（用户）")
    public Result<String> submitWithdrawal(@RequestBody WithdrawalRequest request) {
        try {
            userService.submitWithdrawal(request);
            return Result.success("提现申请已提交");
        } catch (Exception e) {
            return Result.failure("提交失败：" + e.getMessage());
        }
    }

    @GetMapping("/withdrawals")
    @Operation(summary = "后台查看所有提现记录（身份“SALES，BOSS“）")
    public Result<List<WithdrawalRecord>> getAllWithdrawals() {
        List<WithdrawalRecord> records = withdrawalRecordRepository.findAllByOrderByWithdrawalTimeDesc();
        return Result.success(records);
    }

    @PostMapping("/withdraw/approve")
    @Operation(summary = "审核通过提现（身份“SALES，BOSS“）")
    public Result<String> approveWithdrawal(@RequestBody ApproveWithdrawalRequest request) {
        try {
            userService.approveWithdrawal(request.getWithdrawalId());
            return Result.success("提现已通过并扣款成功");
        } catch (Exception e) {
            return Result.failure("操作失败：" + e.getMessage());
        }
    }


    @PostMapping("/withdraw/reject")
    @Operation(summary = "拒绝提现（身份“SALES，BOSS“）")
    public Result<String> rejectWithdrawal(@RequestBody RejectWithdrawalRequest request) {
        try {
            userService.rejectWithdrawal(request.getWithdrawalId(), request.getReason());
            return Result.success("已拒绝提现：" + request.getReason());
        } catch (Exception e) {
            return Result.failure("操作失败：" + e.getMessage());
        }
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "根据用户名查询用户")
    public Result<User> getUserByUsername(@PathVariable String username) {
        return userRepository.findByUsername(username)
                .map(Result::success)
                .orElse(Result.notFound("用户不存在"));
    }

    @GetMapping("/phone/{phone}")
    @Operation(summary = "根据手机号查询用户")
    public Result<User> getUserByPhone(@PathVariable String phone) {
        return userRepository.findByPhone(phone)
                .map(Result::success)
                .orElse(Result.notFound("用户不存在"));
    }


    /**
     * 更新用户指定字段
     *
     * @param userId   用户ID
     * @param field    要更新的字段名称（如 "password", "superiorUsername", "tronWalletAddress" 等）
     * @param newValue 新的字段值
     * @return 更新后的用户信息或失败信息
     */
    @PutMapping("/update-field")
    @Operation(summary = "更新指定用户的指定字段", description = "根据字段名更新用户的信息，如登录密码、上级用户名、Tron钱包地址等")
    public Result<User> updateUserField(
            @RequestParam Long userId,
            @RequestParam String field,
            @RequestParam String newValue
    ) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("用户未找到"));

            switch (field) {
                case "password":
                    user.setPassword(newValue);
                    break;
                case "superiorUsername":
                    user.setSuperiorUsername(newValue);
                    break;
                case "tronWalletAddress":
                    user.setTronWalletAddress(newValue);
                    break;
                case "bitCoinWalletAddress":
                    user.setBitCoinWalletAddress(newValue);
                    break;
                case "ethWalletAddress":
                    user.setEthWalletAddress(newValue);
                    break;
                case "coinWalletAddress":
                    user.setCoinWalletAddress(newValue);
                    break;
                default:
                    return Result.failure("不支持更新该字段");
            }

            // 保存更新后的用户信息
            userRepository.save(user);

            return Result.success(user);
        } catch (Exception e) {
            log.error("更新用户字段失败", e);
            return Result.failure("更新失败：" + e.getMessage());
        }
    }


}


