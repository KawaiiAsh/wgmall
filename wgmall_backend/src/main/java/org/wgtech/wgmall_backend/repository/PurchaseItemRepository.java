package org.wgtech.wgmall_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.wgtech.wgmall_backend.entity.PurchaseItem;
import org.wgtech.wgmall_backend.entity.PurchaseRequest;

public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Long> {

    Page<PurchaseItem> findByRequest_Buyer_Id(Long buyerId, Pageable pageable);

    Page<PurchaseItem> findByRequest_Buyer_IdAndRequest_Status(Long buyerId, PurchaseRequest.Status status, Pageable pageable);
}
