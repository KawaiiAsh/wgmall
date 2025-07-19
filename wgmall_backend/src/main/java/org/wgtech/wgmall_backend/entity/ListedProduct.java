package org.wgtech.wgmall_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 上架商品实体类。
 * 表示某个商家将某个平台商品以某个价格上架的记录。
 * 映射数据库表：listed_product
 */
@Entity
@Table(name = "listed_product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListedProduct {

    /**
     * 主键，自增 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 所属店铺（哪个商家上架的）
     * 外键关联 Shop 表
     */
    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    /**
     * 对应的平台商品（商品基础信息）
     * 外键关联 Product 表
     */
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * 卖家设置的上架售价（区别于平台商品的原价）
     */
    @Column(nullable = false)
    private BigDecimal salePrice;

    /**
     * 上架时间（商品被商家挂出时的时间）
     */
    private Date listedAt;

    /**
     * 是否处于上架状态：
     * true 表示正在销售（上架中），false 表示已下架或禁售。
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;


    @Column(nullable = false)
    private BigDecimal costPrice; // ✅ 新增：成本价（= 商品原价 × (1 - 返点)）

}
