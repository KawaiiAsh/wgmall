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

    public enum OrderStatus {
        PENDING,
        PROCESSING,
        SHIPPED,
        WAREHOUSE,
        TRANSPORTING,
        DELIVERED,
        COMPLETED,
        REFUNDABLE,   // 表示可退款状态
        REFUNDING,    // 表示正在退款中
        REFUNDED      // 可新增此状态以明确表示已退款
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;

    private Long userId;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;

    private BigDecimal totalAmount;

    private BigDecimal totalWholesale;

    private BigDecimal totalSale;

    private BigDecimal totalProfit;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // 可选字段：用于记录退款原因
    private String refundReason;

    private String customerName;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
