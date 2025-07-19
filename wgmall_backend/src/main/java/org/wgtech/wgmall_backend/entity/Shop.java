package org.wgtech.wgmall_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 商铺实体类。
 * 每个商铺由一个用户拥有，并包含店铺信息、主营商品类型、联系方式等。
 * 映射到数据库表：shop
 */
@Entity
@Table(name = "shop")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shop {

    /**
     * 主键，自增长 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 所属用户（店主）
     * 一对一关联 User 表，一个用户只能拥有一个店铺
     */
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    /**
     * 店铺名称
     */
    @Column(nullable = false)
    private String name;

    /**
     * 店铺介绍（最长 1000 字符）
     */
    @Column(length = 1000)
    private String description;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 主营商品类型（枚举）
     * 使用 @ElementCollection 存储为一张关联表（shop_main_product_types），支持多个主营分类
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<Product.ProductType> mainProductTypes;

    /**
     * 店铺最多可同时上架的商品数量（默认 10）
     */
    private int maxListings = 10;

    @Column(nullable = false)
    private Double rebateRate; // 返点比例：0.00 ~ 0.20（表示0%~20%） // 例如 0.15 表示 15%

}
