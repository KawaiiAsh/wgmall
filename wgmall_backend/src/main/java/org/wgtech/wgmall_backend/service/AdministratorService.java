package org.wgtech.wgmall_backend.service;

import org.wgtech.wgmall_backend.entity.Administrator;

import java.util.List;

public interface AdministratorService {

    // 查询所有业务员
    List<Administrator> getAllSales();

}
