package org.wgtech.wgmall_backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminWithdrawalRequest {
    private Long userId;
    private BigDecimal amount;
    private String remark;         // 可选，备注说明
    private String withdrawalDate; // yyyy-MM-dd，由客服手动填写
}
