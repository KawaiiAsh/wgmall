package org.wgtech.wgmall_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wgtech.wgmall_backend.entity.CartItem;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserId(Long userId);
}
