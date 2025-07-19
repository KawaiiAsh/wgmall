package org.wgtech.wgmall_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 采购请求实体类。
 * 表示平台后台发起的、针对某个商家店铺的模拟订单请求。
 * 商家可在前台查看到这些订单并选择付款进货，随后由平台控制状态流转。
 *
 * 对应数据库表：purchase_request
 */
@Entity
@Table(name = "purchase_request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseRequest {

    /**
     * 主键，自增长ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 商家用户（虽然字段名叫 buyer，但这里 buyer 实际上是进货的“商家”）
     * 一个商家可以拥有多个采购请求
     */
    @ManyToOne
    private User buyer;

    /**
     * 采购请求面向的店铺（即商家绑定的唯一店铺）
     */
    @ManyToOne
    private Shop shop;

    private String customerName;     // 买家名称（由客服填写）

    /**
     * 采购状态（由平台控制流转）
     * 枚举：PENDING、PROCESSING、SHIPPED、WAREHOUSE、TRANSPORTING、DELIVERED、COMPLETED
     */
    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * 总批发价（平台成本价 × 数量）
     */
    private BigDecimal totalWholesale;

    /**
     * 总售价（商家设置的出售单价 × 数量）
     */
    private BigDecimal totalSale;

    /**
     * 总利润（totalSale - totalWholesale）
     */
    private BigDecimal totalProfit;

    /**
     * 创建时间（由平台客服发起采购请求时生成）
     */
    private Date createdAt;

    /**
     * 所有采购项（子项目），即本次采购包含的所有商品
     * 级联保存：保存 request 时会同步保存 items
     */
    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL)
    private List<PurchaseItem> items;

    /**
     * 采购状态枚举定义
     */
    public enum Status {
        /**
         * 商家未付款（等待进货）
         */
        PENDING,

        /**
         * 商家已付款，待处理
         */
        PROCESSING,

        /**
         * 已发货
         */
        SHIPPED,

        /**
         * 到达仓库
         */
        WAREHOUSE,

        /**
         * 配送中
         */
        TRANSPORTING,

        /**
         * 已送达
         */
        DELIVERED,

        /**
         * 已完成：利润结算已返还商家
         */
        COMPLETED
    }
}
