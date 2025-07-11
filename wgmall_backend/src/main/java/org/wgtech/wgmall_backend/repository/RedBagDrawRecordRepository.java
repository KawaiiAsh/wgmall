package org.wgtech.wgmall_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.wgtech.wgmall_backend.entity.RedBagDrawRecord;

import java.util.Optional;

@Repository
public interface RedBagDrawRecordRepository extends JpaRepository<RedBagDrawRecord, Long> {
    Optional<RedBagDrawRecord> findTopByUserIdOrderByDrawTimeDesc(Long userId);
}
