package org.wgtech.wgmall_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.wgtech.wgmall_backend.entity.ShippingItem;

import java.util.List;

public interface ShippingItemRepository extends JpaRepository<ShippingItem, Long> {
    Page<ShippingItem> findByRequest_Shop_NameContainingAndStatus(String shopName, ShippingItem.ShippingStatus status, Pageable pageable);

    // 按卖家 ID + 状态分页
    Page<ShippingItem> findByRequest_Buyer_IdAndStatus(Long buyerId, ShippingItem.ShippingStatus status, Pageable pageable);

    // 只按卖家 ID 分页（不筛状态）
    Page<ShippingItem> findByRequest_Buyer_Id(Long buyerId, Pageable pageable);
}
