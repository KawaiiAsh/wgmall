package org.wgtech.wgmall_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "purchase_request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User buyer; // 实际上是卖家

    @ManyToOne
    private Shop shop;

    @Enumerated(EnumType.STRING)
    private Status status;

    private BigDecimal totalWholesale;
    private BigDecimal totalSale;
    private BigDecimal totalProfit;

    private Date createdAt;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL)
    private List<PurchaseItem> items;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL)
    private List<ShippingItem> shippingItems;
    // ✅ 内部枚举
    public enum Status {
        PENDING,
        PROCESSING,
        SHIPPED,
        WAREHOUSE,
        TRANSPORTING,
        DELIVERED,
        COMPLETED
    }
}
