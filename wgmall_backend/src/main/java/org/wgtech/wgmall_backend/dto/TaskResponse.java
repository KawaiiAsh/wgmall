package org.wgtech.wgmall_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Long taskId;                     // 日志ID
    private Long productId;                 // 商品ID
    private BigDecimal productAmount;       // 商品金额
    private String dispatchType;            // 派单类型：RANDOM / ASSIGNED / RESERVED
    private boolean needToPay;              // 是否需要立即付款
}
