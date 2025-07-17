package org.wgtech.wgmall_backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminRechargeRequest {
    private Long userId;
    private BigDecimal amount;
    private String remark;
    private String rechargeDate; // 格式：yyyy-MM-dd（由客服填写）
}
