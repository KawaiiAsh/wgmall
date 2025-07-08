package org.wgtech.wgmall_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wgtech.wgmall_backend.entity.Administrator;
import org.wgtech.wgmall_backend.repository.AdministratorRepository;
import org.wgtech.wgmall_backend.service.AdministratorService;

import java.util.Optional;

@Service
public class AdministratorServiceImpl implements AdministratorService {

    @Autowired
    private AdministratorRepository administratorRepository;

    /**
     * 检测业务员是否存在
     * @param username
     * @return
     */
    @Override
    public boolean checkIfSalespersonExists(String username) {
        Optional<Administrator> administrator = administratorRepository.findByUsername(username);
        return administrator.isPresent();  // 如果存在，则返回true，否则返回false
    }

}
