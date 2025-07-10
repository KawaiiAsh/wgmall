package org.wgtech.wgmall_backend.service;

import org.wgtech.wgmall_backend.entity.Administrator;
import java.util.List;

public interface AdministratorService {

    /**
     * 获取所有业务员信息（管理员中带销售身份的）
     * @return 所有业务员列表
     */
    List<Administrator> getAllSales();

}
