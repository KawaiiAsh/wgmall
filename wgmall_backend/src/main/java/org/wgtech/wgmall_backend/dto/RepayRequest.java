package org.wgtech.wgmall_backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RepayRequest {
    private Long userId;
    private BigDecimal amount;
}
