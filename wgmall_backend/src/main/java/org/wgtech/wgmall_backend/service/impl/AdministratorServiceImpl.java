package org.wgtech.wgmall_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.wgtech.wgmall_backend.entity.Administrator;
import org.wgtech.wgmall_backend.repository.AdministratorRepository;
import org.wgtech.wgmall_backend.service.AdministratorService;
import org.wgtech.wgmall_backend.utils.Result;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 管理员服务实现类
 * 处理与管理员相关的业务逻辑
 */
@Service
public class AdministratorServiceImpl implements AdministratorService {

    @Autowired
    private AdministratorRepository administratorRepository;

    /**
     * 获取所有角色为 SALES（业务员）的管理员账户
     * 用于前端展示、权限控制等
     *
     * @return 所有业务员账号的列表
     */
    @Override
    public List<Administrator> getAllSales() {
        return administratorRepository.findByRole(Administrator.Role.SALES);
    }

    @Override
    public void banAdministrator(long id) {
        Optional<Administrator> optionalAdmin = administratorRepository.findById(id);
        if (optionalAdmin.isPresent()) {
            Administrator admin = optionalAdmin.get();
            admin.setBanned(true);  // 设置封禁
            administratorRepository.save(admin);  // 更新数据库
        } else {
            throw new RuntimeException("管理员不存在，ID: " + id);
        }
    }

    @Override
    public void unbanAdministrator(long id) {
        Administrator admin = administratorRepository.findById(id)
                .filter(a -> a.getRole().name().equalsIgnoreCase("SALES"))
                .orElseThrow(() -> new RuntimeException("业务员不存在"));

        admin.setBanned(false);
        administratorRepository.save(admin);
    }


    @Override
    public Administrator createBoss(String username, String nickname, String password) {
        if (administratorRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("用户名已存在");
        }
        if (administratorRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("昵称已存在");
        }

        Administrator boss = new Administrator();
        boss.setUsername(username);
        boss.setNickname(nickname);
        boss.setPassword(password); // 实际项目建议加密
        boss.setRole(Administrator.Role.BOSS);
        boss.setBanned(false);
        return administratorRepository.save(boss);
    }

    @Override
    public List<Administrator> getAllBosses() {
        return administratorRepository.findByRole(Administrator.Role.BOSS);
    }

    @Override
    public Page<Administrator> getAllSalesDesc(int page, int size) {
        return administratorRepository.findByRoleOrderByIdDesc(Administrator.Role.SALES, PageRequest.of(page, size));
    }

    @Override
    public Page<Administrator> searchSalesByNickname(String keyword, int page, int size) {
        return administratorRepository.findByRoleAndNicknameContainingIgnoreCaseOrderByIdDesc(
                Administrator.Role.SALES, keyword, PageRequest.of(page, size));
    }

    @Override
    public Result<Administrator> findSalesById(long id) {
        Administrator admin = administratorRepository.findById(id)
                .filter(a -> "SALES".equalsIgnoreCase(String.valueOf(a.getRole())))
                .orElseThrow(() -> new RuntimeException("未找到该业务员"));

        return Result.success(admin);
    }

}
