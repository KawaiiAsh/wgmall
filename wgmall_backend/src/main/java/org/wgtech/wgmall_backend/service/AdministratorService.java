package org.wgtech.wgmall_backend.service;

public interface AdministratorService {

    // 检查业务员是否存在
    boolean checkIfSalespersonExists(String username);
}
