package org.wgtech.wgmall_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wgtech.wgmall_backend.entity.SellerApplication;

public interface SellerApplicationRepository extends JpaRepository<SellerApplication, Long> {
    boolean existsByUserId(Long id);
}
