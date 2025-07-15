package org.wgtech.wgmall_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private PurchaseRequest request; // 属于哪个进货单

    @ManyToOne
    private ListedProduct listedProduct; // 对应哪个商品

    private int quantity;

    @Enumerated(EnumType.STRING)
    private ShippingStatus status; // 发货状态


    public enum ShippingStatus {
        PENDING,
        PROCESSING,
        SHIPPED,
        WAREHOUSE,
        TRANSPORTING,
        DELIVERED,
        COMPLETED
    }
}
