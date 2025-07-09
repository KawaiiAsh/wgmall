package org.wgtech.wgmall_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.service.UserService;
import org.wgtech.wgmall_backend.utils.JwtUtils;
import org.wgtech.wgmall_backend.utils.Result;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 注册功能
     * @param username 用户名
     * @param phone 手机号
     * @param password 密码
     * @param inviteCode 邀请码
     * @param fundPassword 资金密码
     * @param ip 用户的IP地址（前端传递）
     * @return 注册结果的提示
     */
    @PostMapping("/register")
    public Result<String> register(
            @RequestParam String username,
            @RequestParam String phone,
            @RequestParam String password,
            @RequestParam(required = false) String inviteCode,
            @RequestParam String fundPassword,
            @RequestParam String ip) {  // 接收IP地址参数

        // 调用服务层进行用户注册
        Result<User> result = userService.registerUser(username, phone, password, inviteCode, fundPassword, ip);

        if (result.getCode() == 200) {
            // 注册成功，返回用户ID
            return Result.success("注册成功，用户ID：" + result.getData().getId());
        } else {
            // 如果注册失败，返回失败消息
            return Result.failure(result.getMessage());
        }
    }

    /**
     * 登录功能
     * @param usernameOrPhone 用户名或手机号
     * @param password 密码
     * @return 登录结果的提示
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(
            @RequestParam String usernameOrPhone,
            @RequestParam String password) {

        // 调用服务层进行用户登录
        Result<User> result = userService.loginUser(usernameOrPhone, password);

        if (result.getCode() == 200) {
            // 登录成功，生成 JWT token
            User user = result.getData();
            String token = JwtUtils.generateToken(user.getUsername()); // 你已有 JwtUtils

            // 返回 token 和用户信息
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user); // 可返回基本用户信息

            return Result.success(data);
        } else {
            // 登录失败，返回错误信息
            return Result.failure(result.getMessage());
        }
    }
}
