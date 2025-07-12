package org.wgtech.wgmall_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    public enum RefundStatus {
        REFUNDABLE,  // 可退款
        REFUNDING    // 用户申请后
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 下单用户
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 下单商品
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // 商品数量
    @Column(nullable = false)
    private Integer quantity;

    // 总金额
    @Column(nullable = false)
    private BigDecimal totalAmount;

    // 固定发货状态
    @Column(nullable = false)
    private String shipStatus = "Processing";

    // 可退款 or 退款中
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundStatus refundStatus = RefundStatus.REFUNDABLE;

    // 用户填写的退款理由
    private String refundReason;

    // 创建时间
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
