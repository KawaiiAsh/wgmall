package org.wgtech.wgmall_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 所属用户（多对一）
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 商品
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // 数量
    @NotNull
    private Integer quantity;

    // 加入购物车时间（可选）
    private Date addedTime;
}
