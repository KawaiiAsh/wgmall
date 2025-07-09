package org.wgtech.wgmall_backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    public enum ProductType {
        ELECTRONICS,    // 电子产品
        FOOD,           // 食品
        CLOTHING,       // 服饰
        BEAUTY,         // 美妆个护
        BOOKS,          // 图书
        DIGITAL,        // 虚拟商品/软件
        TOYS,           // 玩具
        FURNITURE,      // 家具
        SPORTS,         // 体育用品
        JEWELRY,        // 首饰
        HOME_APPLIANCE, // 家电
        PET_SUPPLIES,   // 宠物用品
        STATIONERY,     // 文具办公
        CAR_ACCESSORY,  // 汽车用品
        HEALTH,         // 健康保健
        BABY,           // 母婴
        GROCERY,        // 杂货
        OTHER           // 其他
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 商品ID

    @NotNull(message = "商品名称不能为空")
    private String name;  // 商品名称

    @NotNull(message = "价格不能为空")
    private BigDecimal price;  // 商品价格

    @Lob
    private String description;  // 商品详情

    @NotNull(message = "库存不能为空")
    private Integer stock;  // 库存

    @NotNull(message = "销量不能为空")
    private Integer sales;  // 销量

    @NotNull(message = "商品类型不能为空")
    @Enumerated(EnumType.STRING)
    private ProductType type;  // 商品类型（枚举）

    private String uploader;  // 上架人（默认是“业务员”）

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonManagedReference // 与上面配对使用
    private List<ProductImage> images;  // 商品轮播图

    @PrePersist
    public void setDefaultUploader() {
        if (this.uploader == null || this.uploader.trim().isEmpty()) {
            this.uploader = "业务员";
        }
    }
}
