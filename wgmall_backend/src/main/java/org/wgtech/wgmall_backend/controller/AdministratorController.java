package org.wgtech.wgmall_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wgtech.wgmall_backend.entity.Administrator;
import org.wgtech.wgmall_backend.utils.SalespersonCreator;
import org.wgtech.wgmall_backend.utils.Result;

@RestController
@RequestMapping("/apply")
public class AdministratorController {

    @Autowired
    private SalespersonCreator salespersonCreator;

    // 创建业务员的接口
    @PostMapping("/createsales")
    public Result createSalesperson(@RequestParam String username,
                                    @RequestParam String nickname,
                                    @RequestParam String password) {
        try {
            Administrator admin = salespersonCreator.createSalesperson(username, nickname, password);
            return Result.success(admin);  // 创建成功，返回业务员信息
        } catch (Exception e) {
            return Result.failure("创建业务员失败：" + e.getMessage());
        }
    }
}
