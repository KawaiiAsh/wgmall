package org.wgtech.wgmall_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wgtech.wgmall_backend.entity.Order;
import org.wgtech.wgmall_backend.entity.Product;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.repository.OrderRepository;
import org.wgtech.wgmall_backend.repository.ProductRepository;
import org.wgtech.wgmall_backend.repository.UserRepository;
import org.wgtech.wgmall_backend.service.OrderService;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;
    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void requestRefund(Long orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (order.getStatus() != Order.OrderStatus.REFUNDABLE) {
            throw new RuntimeException("该订单不可退款");
        }

        order.setStatus(Order.OrderStatus.REFUNDING);
        order.setRefundReason(reason);

        orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order createOrder(Long userId, Long productId, Integer quantity, BigDecimal totalAmount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        // 检查用户余额是否充足
        if (user.getBalance().compareTo(totalAmount) < 0) {
            throw new RuntimeException("余额不足，无法完成付款");
        }

        // 扣除用户余额
        user.setBalance(user.getBalance().subtract(totalAmount));
        userRepository.save(user); // 更新余额

        Order order = Order.builder()
                .buyer(user)
                .product(product)
                .quantity(quantity)
                .totalAmount(totalAmount)
                .status(Order.OrderStatus.PROCESSING)
                .build();



        return orderRepository.save(order);
    }


}
