package org.wgtech.wgmall_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wgtech.wgmall_backend.entity.RechargeRecord;

import java.util.List;

public interface RechargeRecordRepository extends JpaRepository<RechargeRecord, Long> {
    List<RechargeRecord> findByUserIdOrderByRechargeTimeDesc(Long userId);
}
