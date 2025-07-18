package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.wgtech.wgmall_backend.dto.AdminLoginRequest;
import org.wgtech.wgmall_backend.dto.ChangePasswordRequest;
import org.wgtech.wgmall_backend.dto.LoginRequest;
import org.wgtech.wgmall_backend.dto.RegisterRequest;
import org.wgtech.wgmall_backend.entity.Administrator;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.service.AdministratorService;
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

    @Autowired
    AdministratorService administratorService;
    /**
     * 用户注册接口
     *
     * 接口地址：POST /auth/register
     * 通过用户名、手机号、密码、邀请码、资金密码等参数注册新用户
     *
     * @return 注册成功提示或失败原因
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "根据用户名、手机号、密码、邀请码等信息注册新用户，成功返回 token")
    public Result<Map<String, Object>> register(@RequestBody RegisterRequest req) {
        Result<User> result = userService.registerUser(
                req.getUsername(),
                req.getPhone(),
                req.getPassword(),
                req.getInvitecode(),
                req.getFundpassword(),
                req.getIp()
        );

        if (result.getCode() == 200) {
            User user = result.getData();
            String role = user.getBuyerOrSaler() == 0 ? "BUYER" : "SALER";
            String token = JwtUtils.generateToken(user.getUsername(), role, "USER");

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);
            data.put("identity", role);  // 返回角色标识（BUYER 或 SELLER）
            return Result.success(data);
        } else {
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
    public Result<Map<String, Object>> login(@RequestBody LoginRequest req) {
        Result<User> result = userService.loginUser(req.getUsername_or_phone(), req.getPassword());

        if (result.getCode() == 200) {
            User user = result.getData();

            // 根据 buyerOrSaler 字段确定用户是 BUYER 还是 SELLER
            String role = user.getBuyerOrSaler() == 0 ? "BUYER" : "SELLER"; // BUYER 或 SELLER

            // 生成包含角色的 JWT
            String token = JwtUtils.generateToken(user.getUsername(), role, "USER");

            // 返回 token 和用户身份
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);
            data.put("identity", role);  // 返回角色标识（BUYER 或 SELLER）

            return Result.success(data);
        } else {
            return Result.failure(result.getMessage());
        }
    }


    /**
     * 修改登录密码（根据用户名）
     *
     * 接口地址：POST /auth/change-password
     */
    @PostMapping("/change-password")
    @Operation(summary = "修改密码（身份“BOSS”的权限）", description = "给客服修改密码用的，根据用户名直接修改密码，不校验旧密码")
    public Result<String> changePassword(@RequestBody ChangePasswordRequest req) {
        boolean success = userService.changePasswordByUsername(req.getUsername(), req.getNewPassword());
        if (success) {
            return Result.success("密码修改成功");
        } else {
            return Result.failure("用户不存在，修改失败");
        }
    }


    @PostMapping("/login-admin")
    @Operation(summary = "业务员登录", description = "给业务员的专用登录接口")
    public Result<Map<String, Object>> loginAdmin(@RequestBody AdminLoginRequest req) {
        Result<Administrator> result = administratorService.loginAdmin(req.getUsername(), req.getPassword());

        if (result.getCode() == 200) {
            Administrator admin = result.getData();
            String role = admin.getRole().name(); // ROLE_SALES
            String token = JwtUtils.generateToken(admin.getUsername(), role, "ROLE_SALES");

            // Add identity to the response
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("admin", admin);
            data.put("identity", role); // Add identity field (e.g., ROLE_SALES)

            return Result.success(data);
        } else {
            return Result.failure(result.getMessage());
        }
    }


    @PostMapping("/login-boss")
    @Operation(summary = "客服登录", description = "给客服的专用登录接口")
    public Result<Map<String, Object>> loginBoss(@RequestBody AdminLoginRequest req) {
        Result<Administrator> result = administratorService.loginAdmin(req.getUsername(), req.getPassword());

        if (result.getCode() == 200) {
            Administrator admin = result.getData();
            String role = admin.getRole().name(); // ROLE_BOSS
            String token = JwtUtils.generateToken(admin.getUsername(), role, "ROLE_BOSS");

            // Add identity to the response
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("admin", admin);
            data.put("identity", role); // Add identity field (e.g., ROLE_BOSS)

            return Result.success(data);
        } else {
            return Result.failure(result.getMessage());
        }
    }

    /**
     * 用户退出登录接口
     *
     * @return 成功提示（前端应主动清除 token）
     */
    @PostMapping("/logout")
    @Operation(summary = "用户退出登录", description = "前端清除 token 即可，后端可用于记录日志等")
    public Result<String> logoutUser() {
        // 可选：添加 token 黑名单处理或日志记录
        return Result.success("用户已成功退出登录");
    }

    /**
     * 业务员退出登录接口
     */
    @PostMapping("/logout-admin")
    @Operation(summary = "业务员退出登录", description = "前端清除 token 即可，后端记录日志或状态")
    public Result<String> logoutAdmin() {
        // 可选：添加 token 黑名单处理或记录退出时间等
        return Result.success("业务员已成功退出登录");
    }

    /**
     * 客服退出登录接口
     */
    @PostMapping("/logout-boss")
    @Operation(summary = "客服退出登录", description = "前端清除 token 即可，后端记录退出操作")
    public Result<String> logoutBoss() {
        // 可选：添加日志记录或行为分析处理
        return Result.success("客服已成功退出登录");
    }

    @GetMapping("/info")
    @Operation(summary = "获取当前登录用户信息", description = "解析 token，返回管理员信息")
    public Result<?> getCurrentAdminInfo() {
        // 从 Spring Security 的上下文中获取登录信息
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 判断是否为管理员（Administrator 是你的实体类）
        if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            String username = userDetails.getUsername(); // token 中的 sub 是 username
            Administrator admin = administratorService.findByUsername(username);

            if (admin == null) {
                return Result.failure("未找到该管理员");
            }

            // 只返回需要的字段
            Map<String, Object> data = new HashMap<>();
            data.put("id", admin.getId());
            data.put("username", admin.getUsername());
            data.put("nickname", admin.getNickname());
            data.put("invite_code", admin.getInviteCode());
            data.put("role", admin.getRole());
            data.put("banned", admin.isBanned());

            return Result.success(data);
        }

        return Result.failure("未登录或权限异常");
    }


}
