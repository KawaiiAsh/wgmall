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
        WOMEN_CLOTHING,//女衣服
        WOMEN_SHOES,//女鞋
        MENS_CLOTHING,//男衣服
        MENS_SHOES,//男鞋
        HOME_GOODS,//家具
        JEWELRY_WATCHES,//手表
        TOYS,//儿童
        ELECTRONICS,//手机
        GIFT_CARD,//礼品卡
        OFFICE_SUPPLIES,//办公
        BEAUTY_PRODUCTS,//化妆品
        HEAD_ORNAMENT//首饰
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 商品ID

    @NotNull(message = "商品名称不能为空")
    private String name;  // 商品名称

    @NotNull(message = "价格不能为空")
    private BigDecimal price;  // 商品价格

    @NotNull(message = "库存不能为空")
    private Integer stock;  // 库存

    @NotNull(message = "销量不能为空")
    private Integer sales;  // 销量

    @NotNull(message = "商品类型不能为空")
    @Enumerated(EnumType.STRING)
    private ProductType type;  // 商品类型（枚举）

    private String uploader;  // 上架人（默认是“业务员”）

    @NotNull(message = "图片路径不能为空")
    private String imagePath;

    @PrePersist
    public void setDefaultUploader() {
        if (this.uploader == null || this.uploader.trim().isEmpty()) {
            this.uploader = "业务员";
        }
    }
}
