package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wgtech.wgmall_backend.entity.Administrator;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.service.AdministratorService;
import org.wgtech.wgmall_backend.service.UserService;
import org.wgtech.wgmall_backend.utils.SalespersonCreator;
import org.wgtech.wgmall_backend.utils.Result;

import java.util.List;

@RestController // 声明这是一个 REST 控制器，返回值默认以 JSON 形式响应
@RequestMapping("/administrator") // 设置请求路径前缀为 /administrator
@Tag(name = "工作人员接口", description = "创建，封禁，获取所有业务员，客服的功能") // Swagger 文档标签
public class AdministratorController {

    @Autowired
    private SalespersonCreator salespersonCreator; // 工具类，用于创建业务员对象

    @Autowired
    private AdministratorService administratorService; // 管理员服务，用于查询业务员列表


    /**
     * 创建业务员账号
     *
     * 接口地址：POST /administrator/createsales
     * 管理员通过此接口创建业务员账号，传入用户名、昵称和密码。
     *
     * @param username 用户名，必须唯一
     * @param nickname 业务员昵称
     * @param password 登录密码
     * @return 操作结果，包含成功时返回的业务员信息或失败提示
     */
    @PostMapping("/createsales")
    @Operation(
            summary = "创建业务员账号",
            description = "管理员调用此接口可创建一个新的业务员账号，包含用户名、昵称和密码。"
    )
    public Result<Administrator> createSalesperson(
            @Parameter(description = "用户名（唯一）", required = true)
            @RequestParam String username,

            @Parameter(description = "业务员昵称", required = true)
            @RequestParam String nickname,

            @Parameter(description = "登录密码", required = true)
            @RequestParam String password
    ) {
        try {
            // 校验逻辑（可选）：空值、长度等
            if (username.isBlank() || password.isBlank() || nickname.isBlank()) {
                return Result.failure("用户名、密码、昵称不能为空");
            }

            Administrator admin = salespersonCreator.createSalesperson(username, nickname, password);
            return Result.success(admin);
        } catch (IllegalArgumentException ex) {
            return Result.custom(400, "参数非法：" + ex.getMessage(), null);
        } catch (Exception e) {
            return Result.failure("创建业务员失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有业务员账号
     *
     * 接口地址：GET /administrator/sales
     * 查询系统中所有角色为 SALES 的管理员账号（即业务员列表）
     *
     * @return 所有业务员的列表封装在统一返回结构中
     */
    @GetMapping("/sales")
    @Operation(summary = "获取所有业务员", description = "列出所有角色为SALES的管理员")
    public Result<List<Administrator>> getAllSales() {
        List<Administrator> sales = administratorService.getAllSales(); // 查询业务员列表
        return Result.success(sales);  // 返回结果
    }

    /**
     * 封禁管理员账号
     *
     * 接口地址：POST /administrator/ban/{id}
     * 管理员可以调用此接口来封禁指定的管理员账号
     *
     * @param id 管理员ID
     * @return 操作结果
     */
    @PostMapping("/bansales/{id}")
    @Operation(summary = "封禁业务员账号", description = "将指定业务员账号的 isBanned 设置为 true")
    public Result<Void> banAdministrator(@PathVariable int id) {
        try {
            administratorService.banAdministrator(id);
            return Result.success();
        } catch (Exception e) {
            return Result.failure("封禁失败：" + e.getMessage());
        }
    }

    /**
     * 创建管理员（BOSS）账号
     */
    @PostMapping("/createboss")
    @Operation(summary = "创建管理员账号", description = "用于创建角色为BOSS的管理员账号")
    public Result<Administrator> createBoss(
            @RequestParam String username,
            @RequestParam String nickname,
            @RequestParam String password
    ) {
        try {
            if (username.isBlank() || nickname.isBlank() || password.isBlank()) {
                return Result.failure("用户名、密码、昵称不能为空");
            }
            Administrator boss = administratorService.createBoss(username, nickname, password);
            return Result.success(boss);
        } catch (IllegalArgumentException e) {
            return Result.failure("创建失败：" + e.getMessage());
        } catch (Exception e) {
            return Result.failure("系统异常：" + e.getMessage());
        }
    }

    /**
     * 查询所有管理员（BOSS）
     */
    @GetMapping("/bosses")
    @Operation(summary = "获取所有管理员", description = "列出所有角色为BOSS的管理员账号")
    public Result<List<Administrator>> getAllBosses() {
        List<Administrator> bosses = administratorService.getAllBosses();
        return Result.success(bosses);
    }

}
