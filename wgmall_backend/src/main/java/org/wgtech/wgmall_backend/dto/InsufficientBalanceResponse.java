package org.wgtech.wgmall_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsufficientBalanceResponse {
    private BigDecimal requiredAmount;  // 本任务商品金额
    private BigDecimal currentBalance;  // 当前余额
    private BigDecimal shortage;        // 差多少钱
    private Long taskId;                // 当前任务ID
}
