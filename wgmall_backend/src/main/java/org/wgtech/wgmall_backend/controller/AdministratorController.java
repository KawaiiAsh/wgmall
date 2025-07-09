package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wgtech.wgmall_backend.entity.Administrator;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.service.UserService;
import org.wgtech.wgmall_backend.utils.SalespersonCreator;
import org.wgtech.wgmall_backend.utils.Result;

@RestController
@RequestMapping("/administrator")
@Tag(name = "管理员接口", description = "管理员用的相关功能")
public class AdministratorController {

    @Autowired
    private SalespersonCreator salespersonCreator;

    @Autowired
    private UserService userService;
    /**
     * 创建业务员账号
     *
     * @param username 用户名（唯一）
     * @param nickname 昵称
     * @param password 密码
     * @return 创建结果
     */
    @PostMapping("/createsales")
    @Operation(
            summary = "创建业务员账号",
            description = "管理员调用此接口可创建一个新的业务员账号，包含用户名、昵称和密码。"
    )
    public Result createSalesperson(
            @Parameter(description = "用户名（唯一）", required = true)
            @RequestParam String username,

            @Parameter(description = "业务员昵称", required = true)
            @RequestParam String nickname,

            @Parameter(description = "登录密码", required = true)
            @RequestParam String password
    ) {
        try {
            Administrator admin = salespersonCreator.createSalesperson(username, nickname, password);
            return Result.success(admin);  // 创建成功，返回业务员信息
        } catch (Exception e) {
            return Result.failure("创建业务员失败：" + e.getMessage());
        }
    }
}
