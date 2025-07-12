package org.wgtech.wgmall_backend.service;

import org.wgtech.wgmall_backend.entity.Order;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    List<Order> getOrdersByUserId(Long userId);
    void requestRefund(Long orderId, String reason);
    Order createOrder(Long userId, Long productId, Integer quantity, BigDecimal totalAmount);

}
