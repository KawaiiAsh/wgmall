package org.wgtech.wgmall_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.NotNull;

@Entity
@Table(name = "product_images")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 图片ID

    @NotNull(message = "图片路径不能为空")
    private String imagePath;  // 图片本地路径（如 images/products/1/001.jpg）

    private Integer sortOrder;  // 轮播顺序（越小越前）

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonBackReference // 这行解决循环引用
    private Product product;  // 所属商品
}
