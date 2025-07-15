package org.wgtech.wgmall_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.wgtech.wgmall_backend.entity.PurchaseRequest;

import java.util.List;

public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, Long> {
    Page<PurchaseRequest> findByShop_IdAndStatus(Long shopId, PurchaseRequest.Status status, Pageable pageable);

    List<PurchaseRequest> findByBuyer_Id(Long buyerId);

    Page<PurchaseRequest> findByShop_NameContainingAndStatus(String shopName, PurchaseRequest.Status status, Pageable pageable);
    Page<PurchaseRequest> findByShop_NameContaining(String shopName, Pageable pageable);
}