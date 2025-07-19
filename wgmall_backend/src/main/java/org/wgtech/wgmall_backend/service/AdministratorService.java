package org.wgtech.wgmall_backend.service;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.wgtech.wgmall_backend.entity.Administrator;
import org.wgtech.wgmall_backend.utils.Result;

import java.util.List;

public interface AdministratorService {

    /**
     * 获取所有业务员信息（管理员中带销售身份的）
     * @return 所有业务员列表
     */
    List<Administrator> getAllSales();

    void banAdministrator(long id);
    void unbanAdministrator(long id);

    Administrator findByUsername(String username);

    Administrator createBoss(String username, String nickname, String password);

    List<Administrator> getAllBosses();

    Page<Administrator> getAllSalesDesc(int page, int size);

    Page<Administrator> searchSalesByNickname(String keyword, int page, int size);

    Result<Administrator> findSalesById(long id);

    Result<Administrator> loginAdmin(String username, String password);

    void setBanStatus(long id, boolean isBanned);

    List<Administrator> searchSalesByUsernameLike(String keyword);
}
