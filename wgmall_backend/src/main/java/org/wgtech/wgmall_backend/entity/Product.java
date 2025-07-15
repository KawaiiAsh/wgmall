package org.wgtech.wgmall_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    public enum ProductType {
        WOMEN,
        WOMEN_SHOES,
        MENS,
        MENS_SHOES,
        HOME_GOODS,
        JEWELRY_WATCHES,
        TOYS,
        ELECTRONICS,
        GIFT_CARD,
        OFFICE_SUPPLIES,
        BEAUTY_PRODUCTS,
        HEAD_ORNAMENT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "商品名称不能为空")
    private String name;

    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    @NotNull(message = "库存不能为空")
    private Integer stock;

    @NotNull(message = "销量不能为空")
    private Integer sales;

    @NotNull(message = "商品类型不能为空")
    @Enumerated(EnumType.STRING)
    private ProductType type;

    private String uploader;

    @NotNull(message = "图片路径不能为空")
    private String imagePath;

    @PrePersist
    public void setDefaultUploader() {
        if (this.uploader == null || this.uploader.trim().isEmpty()) {
            this.uploader = "业务员";
        }
    }
}
