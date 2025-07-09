package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.service.UserService;
import org.wgtech.wgmall_backend.utils.JwtUtils;
import org.wgtech.wgmall_backend.utils.Result;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证相关接口（注册、登录）
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "认证接口", description = "提供用户注册与登录功能")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册接口
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "根据用户名、手机号、密码、邀请码等信息注册新用户")
    public Result<String> register(
            @Parameter(description = "用户名", required = true)
            @RequestParam String username,

            @Parameter(description = "手机号", required = true)
            @RequestParam String phone,

            @Parameter(description = "登录密码", required = true)
            @RequestParam String password,

            @Parameter(description = "邀请码（必须））",required = true)
            @RequestParam String inviteCode,

            @Parameter(description = "资金密码", required = true)
            @RequestParam String fundPassword,

            @Parameter(description = "用户注册IP地址（由前端传入）", required = true)
            @RequestParam String ip
    ) {
        Result<User> result = userService.registerUser(username, phone, password, inviteCode, fundPassword, ip);

        if (result.getCode() == 200) {
            return Result.success("注册成功，用户ID：" + result.getData().getId());
        } else {
            return Result.failure(result.getMessage());
        }
    }

    /**
     * 用户登录接口
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "通过用户名或手机号 + 密码进行登录，返回Token")
    public Result<Map<String, Object>> login(
            @Parameter(description = "用户名或手机号", required = true)
            @RequestParam String usernameOrPhone,

            @Parameter(description = "登录密码", required = true)
            @RequestParam String password
    ) {
        Result<User> result = userService.loginUser(usernameOrPhone, password);

        if (result.getCode() == 200) {
            User user = result.getData();
            String token = JwtUtils.generateToken(user.getUsername());

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);

            return Result.success(data);
        } else {
            return Result.failure(result.getMessage());
        }
    }
}
