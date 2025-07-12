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
@RestController // 表示这是一个 REST 控制器，返回数据为 JSON 格式
@RequestMapping("/auth") // 所有接口的路径前缀为 /auth
@Tag(name = "认证接口", description = "提供用户注册与登录功能") // Swagger 标签，分组显示
public class AuthController {

    @Autowired
    private UserService userService; // 注入用户服务，处理注册/登录逻辑

    /**
     * 用户注册接口
     *
     * 接口地址：POST /auth/register
     * 通过用户名、手机号、密码、邀请码、资金密码等参数注册新用户
     *
     * @return 注册成功提示或失败原因
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

            @Parameter(description = "邀请码（必须）", required = true)
            @RequestParam String inviteCode,

            @Parameter(description = "资金密码", required = true)
            @RequestParam String fundPassword,

            @Parameter(description = "用户注册IP地址（由前端传入）", required = true)
            @RequestParam String ip
    ) {
        // 调用服务层注册用户
        Result<User> result = userService.registerUser(username, phone, password, inviteCode, fundPassword, ip);

        if (result.getCode() == 200) {
            // 注册成功，返回用户 ID
            return Result.success("注册成功，用户ID：" + result.getData().getId());
        } else {
            // 注册失败，返回错误信息
            return Result.failure(result.getMessage());
        }
    }

    /**
     * 用户登录接口
     *
     * 接口地址：POST /auth/login
     * 用户通过用户名或手机号 + 密码进行登录，成功后返回 JWT Token 和用户信息
     *
     * @return 登录结果，包含 token 和用户信息，或失败信息
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "通过用户名或手机号 + 密码进行登录，返回Token")
    public Result<Map<String, Object>> login(
            @Parameter(description = "用户名或手机号", required = true)
            @RequestParam String usernameOrPhone,

            @Parameter(description = "登录密码", required = true)
            @RequestParam String password
    ) {
        // 验证用户身份
        Result<User> result = userService.loginUser(usernameOrPhone, password);

        if (result.getCode() == 200) {
            User user = result.getData();

            // 登录成功，生成 JWT Token
            String token = JwtUtils.generateToken(user.getUsername());

            // 封装返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);

            return Result.success(data);
        } else {
            // 登录失败，返回错误信息
            return Result.failure(result.getMessage());
        }
    }

    /**
     * 修改登录密码（根据用户名）
     *
     * 接口地址：POST /auth/change-password
     */
    @PostMapping("/change-password")
    @Operation(summary = "修改密码", description = "给客户付用的，根据用户名直接修改密码，不校验旧密码")
    public Result<String> changePassword(
            @Parameter(description = "用户名", required = true)
            @RequestParam String username,

            @Parameter(description = "新密码", required = true)
            @RequestParam String newPassword
    ) {
        boolean success = userService.changePasswordByUsername(username, newPassword);
        if (success) {
            return Result.success("密码修改成功");
        } else {
            return Result.failure("用户不存在，修改失败");
        }
    }


}
