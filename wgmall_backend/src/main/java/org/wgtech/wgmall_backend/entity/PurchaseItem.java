package org.wgtech.wgmall_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 采购项实体类。
 * 表示某个采购请求中的一项商品（类似采购订单的子项），关联已上架商品。
 * 对应数据库表：purchase_item
 */
@Entity
@Table(name = "purchase_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseItem {

    /**
     * 主键，自增长 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 所属的采购请求（外键）
     * 多个 PurchaseItem 属于同一个 PurchaseRequest
     */
    @ManyToOne
    private PurchaseRequest request;

    /**
     * 采购的上架商品（外键）
     * 关联商家上架的商品记录
     */
    @ManyToOne
    private ListedProduct listedProduct;

    /**
     * 采购数量（件数）
     */
    private int quantity;

    /**
     * 批发价（平台或商家给予的采购价）
     */
    private BigDecimal wholesalePrice;

    /**
     * 售价（最终零售价，用于计算利润）
     */
    private BigDecimal salePrice;
}
