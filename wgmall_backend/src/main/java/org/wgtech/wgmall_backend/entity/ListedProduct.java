package org.wgtech.wgmall_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "listed_product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListedProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 所属店铺（哪个商家）
    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    // 对应的平台商品
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // 卖家自定义的上架售价
    @Column(nullable = false)
    private BigDecimal salePrice;

    private Date listedAt;
}
