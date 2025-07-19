package org.wgtech.wgmall_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wgtech.wgmall_backend.entity.WithdrawalRecord;

import java.util.List;

public interface WithdrawalRecordRepository extends JpaRepository<WithdrawalRecord, Long> {
    List<WithdrawalRecord> findByUserIdOrderByWithdrawalTimeDesc(Long userId);

    List<WithdrawalRecord> findAllByOrderByWithdrawalTimeDesc();

    List<WithdrawalRecord> findByUserIdAndStatusOrderByWithdrawalTimeDesc(Long userId, String status);

}
