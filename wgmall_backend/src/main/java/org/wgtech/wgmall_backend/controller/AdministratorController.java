package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.wgtech.wgmall_backend.dto.CreateSalesRequest;
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
     * @return 操作结果，包含成功时返回的业务员信息或失败提示
     */
    @PostMapping("/createsales")
    @Operation(summary = "创建业务员账号✅")
    public Result<Administrator> createSalesperson(
            @RequestBody CreateSalesRequest request
    ) {
        if (request.getUsername().isBlank() ||
                request.getPassword().isBlank() ||
                request.getNickname().isBlank()) {
            return Result.failure("用户名、密码、昵称不能为空");
        }

        Administrator admin = salespersonCreator.createSalesperson(
                request.getUsername(),
                request.getNickname(),
                request.getPassword()
        );
        return Result.success(admin);
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
    @Operation(summary = "封禁业务员账号✅", description = "将指定业务员账号的 isBanned 设置为 true")
    public Result<Void> banAdministrator(@PathVariable int id) {
        try {
            administratorService.banAdministrator(id);
            return Result.success();
        } catch (Exception e) {
            return Result.failure("封禁失败：" + e.getMessage());
        }
    }

    @PostMapping("/unbansales/{id}")
    @Operation(summary = "解封业务员账号✅", description = "将指定业务员账号的 isBanned 设置为 false")
    public Result<Void> unbanAdministrator(@PathVariable int id) {
        try {
            administratorService.unbanAdministrator(id);
            return Result.success();
        } catch (Exception e) {
            return Result.failure("解封失败：" + e.getMessage());
        }
    }

    /**
     * 查询所有管理员（BOSS）
     */
    @GetMapping("/bosses")
    @Operation(summary = "获取所有管理员✅", description = "列出所有角色为BOSS的管理员账号")
    public Result<List<Administrator>> getAllBosses() {
        List<Administrator> bosses = administratorService.getAllBosses();
        return Result.success(bosses);
    }

    @GetMapping("/sales/page")
    @Operation(summary = "分页获取所有业务员✅", description = "按ID倒序分页获取")
    public Result<Page<Administrator>> getSalesPageByIdDesc(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Administrator> result = administratorService.getAllSalesDesc(page, size);
        return Result.success(result);
    }


    @GetMapping("/sales/search")
    @Operation(summary = "根据昵称搜索业务员✅", description = "模糊查询 + 分页 + 倒序")
    public Result<Page<Administrator>> searchSales(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Administrator> result = administratorService.searchSalesByNickname(keyword, page, size);
        return Result.success(result);
    }

    @GetMapping("/sales/{id}")
    @Operation(summary = "根据ID查找业务员✅", description = "返回指定业务员信息")
    public Result<Administrator> findSalesById(@PathVariable long id) {
        return administratorService.findSalesById(id);
    }

}
