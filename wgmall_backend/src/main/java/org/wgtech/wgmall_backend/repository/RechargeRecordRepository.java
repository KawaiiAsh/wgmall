package org.wgtech.wgmall_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.wgtech.wgmall_backend.entity.RechargeRecord;

import java.util.List;

public interface RechargeRecordRepository extends JpaRepository<RechargeRecord, Long> {

    List<RechargeRecord> findByUsernameOrderByRechargeTimeDesc(String username, Pageable pageable);

    Page<RechargeRecord> findByUsername(String username, Pageable pageable);

    long countByUsername(String username);  // 不使用 'user'



}
