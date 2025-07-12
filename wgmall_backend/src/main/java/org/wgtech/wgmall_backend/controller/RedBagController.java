package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wgtech.wgmall_backend.entity.*;
import org.wgtech.wgmall_backend.repository.*;
import org.wgtech.wgmall_backend.utils.Result;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/redbag")
@Tag(name = "抢红包流程接口", description = "实现抢红包所需要的接口")
public class RedBagController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRedBadRepository userRedBadRepository;

    @Autowired
    private RedBagDrawRecordRepository redBagDrawRecordRepository;

    // 签到状态临时缓存：userId -> LocalDate
    private final ConcurrentHashMap<Long, String> signedInToday = new ConcurrentHashMap<>();

    // ================================
    // 1. 用户签到（仅标记状态）
    // ================================
    @PostMapping("/signin/{userId}")
    @Operation(summary = "签到")
    public Result<String> signIn(@PathVariable Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return Result.failure("用户不存在");

        String today = java.time.LocalDate.now().toString();

        if (today.equals(signedInToday.get(userId))) {
            return Result.failure("今天已签到");
        }

        signedInToday.put(userId, today);
        return Result.success("签到成功");
    }

    // ================================
    // 2. 用户领取红包接口
    // ================================
    @PostMapping("/draw/{userId}")
    @Operation(summary = "领取红包")
    public Result<String> drawRedBag(@PathVariable Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) return Result.failure("用户不存在");
        User user = optionalUser.get();

        // 判断是否今日已签到
        String today = java.time.LocalDate.now().toString();
        if (!today.equals(signedInToday.get(userId))) {
            return Result.failure("请先签到再领取红包");
        }

        // 24小时限制（领红包限制）
        Optional<RedBagDrawRecord> lastRecordOpt =
                redBagDrawRecordRepository.findTopByUserIdOrderByDrawTimeDesc(userId);
        if (lastRecordOpt.isPresent()) {
            long hours = (System.currentTimeMillis() - lastRecordOpt.get().getDrawTime().getTime()) / (1000 * 60 * 60);
            if (hours < 24) return Result.failure("每24小时只能领取一次红包");
        }

        if (user.getRedBagCount() >= 7) return Result.failure("您已领取完7天红包");

        int currentDay = user.getRedBagCount() + 1;

        Optional<UserRedBad> configOpt = userRedBadRepository.findByUserId(userId);
        double amount = switch (currentDay) {
            case 1 -> configOpt.map(UserRedBad::getDay1).orElse(10.0);
            case 2 -> configOpt.map(UserRedBad::getDay2).orElse(20.0);
            case 3 -> configOpt.map(UserRedBad::getDay3).orElse(30.0);
            case 4 -> configOpt.map(UserRedBad::getDay4).orElse(40.0);
            case 5 -> configOpt.map(UserRedBad::getDay5).orElse(50.0);
            case 6 -> configOpt.map(UserRedBad::getDay6).orElse(60.0);
            case 7 -> configOpt.map(UserRedBad::getDay7).orElse(70.0);
            default -> currentDay * 10.0;
        };

        // 更新用户数据
        user.setRedBagCount(currentDay);
        user.setRedBagDrawCount(user.getRedBagDrawCount() + 1);
        user.setBalance(user.getBalance().add(BigDecimal.valueOf(amount)));
        userRepository.save(user);

        // 添加领取记录
        redBagDrawRecordRepository.save(
                RedBagDrawRecord.builder()
                        .userId(userId)
                        .drawTime(new Date())
                        .build()
        );

        // ✅ 领取成功后清除签到状态
        signedInToday.remove(userId);

        return Result.success("领取成功，第 " + currentDay + " 天，获得红包 " + amount + " 元");
    }


    // ================================
    // 2. 后台设置用户红包金额接口
    // ================================

    // 设置某个用户的7天红包金额
    @PutMapping("/user-config/{userId}")
    @Operation(summary = "设置用户七天红包")
    public Result<UserRedBad> setUserRedBag(@PathVariable Long userId, @RequestBody UserRedBad input) {
        input.setUserId(userId);
        Optional<UserRedBad> existing = userRedBadRepository.findByUserId(userId);
        if (existing.isPresent()) {
            UserRedBad cfg = existing.get();
            cfg.setDay1(input.getDay1());
            cfg.setDay2(input.getDay2());
            cfg.setDay3(input.getDay3());
            cfg.setDay4(input.getDay4());
            cfg.setDay5(input.getDay5());
            cfg.setDay6(input.getDay6());
            cfg.setDay7(input.getDay7());
            userRedBadRepository.save(cfg);
            return Result.success(cfg);
        } else {
            userRedBadRepository.save(input);
            return Result.success(input);
        }
    }

    // 查询某个用户的红包配置
    @GetMapping("/user-config/{userId}")
    @Operation(summary = "根据id查询用户的红包金额")
    public Result<UserRedBad> getUserRedBag(@PathVariable Long userId) {
        return userRedBadRepository.findByUserId(userId)
                .map(Result::success)
                .orElse(Result.failure("该用户未配置红包金额"));
    }
}
