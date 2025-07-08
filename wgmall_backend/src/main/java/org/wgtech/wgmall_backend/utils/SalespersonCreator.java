package org.wgtech.wgmall_backend.utils;

import org.wgtech.wgmall_backend.entity.Administrator;
import org.wgtech.wgmall_backend.repository.AdministratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SalespersonCreator {

    @Autowired
    private AdministratorRepository administratorRepository;

    @Autowired
    private InviteCodeGenerator inviteCodeGenerator;  // 自动注入InviteCodeGenerator

    // 创建指定的业务员
    public Administrator createSalesperson(String username, String nickname, String password) {
        // 自动生成唯一的邀请码
        String inviteCode = inviteCodeGenerator.generateUniqueInviteCode();

        // 创建业务员对象并设置属性
        Administrator salesperson = new Administrator();
        salesperson.setUsername(username);
        salesperson.setNickname(nickname);
        salesperson.setPassword(password);
        salesperson.setInviteCode(inviteCode);
        salesperson.setRole(Administrator.Role.SALES);  // 设置为业务员角色
        salesperson.setBanned(false);

        // 保存到数据库
        return administratorRepository.save(salesperson);
    }
}
