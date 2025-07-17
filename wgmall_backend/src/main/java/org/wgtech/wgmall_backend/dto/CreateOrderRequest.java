package org.wgtech.wgmall_backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateOrderRequest {
    private Long userId;
    private Long productId;
    private Integer quantity;
    private BigDecimal totalAmount;
}
