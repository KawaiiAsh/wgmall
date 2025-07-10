package org.wgtech.wgmall_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wgtech.wgmall_backend.entity.Administrator;
import org.wgtech.wgmall_backend.repository.AdministratorRepository;
import org.wgtech.wgmall_backend.service.AdministratorService;

import java.util.List;

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
}
