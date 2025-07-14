package org.wgtech.wgmall_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wgtech.wgmall_backend.entity.LoanApplication;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    // 可以根据需要添加一些查询方法
}
