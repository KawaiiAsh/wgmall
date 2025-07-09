package org.wgtech.wgmall_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class OrderTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增
    private Long id;

    private String username;

    private BigDecimal amount;
    private BigDecimal commissionRate;

    @Enumerated(EnumType.STRING)
    private OrderType type;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String productTitle;
    private String productImage;
    private BigDecimal productPrice;

    public enum OrderType {
        NORMAL, ASSIGNED, RESERVED
    }

    public enum OrderStatus {
        PENDING, COMPLETED
    }
}
