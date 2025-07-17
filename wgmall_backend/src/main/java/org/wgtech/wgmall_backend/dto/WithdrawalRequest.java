package org.wgtech.wgmall_backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawalRequest {
    private Long userId;
    private String username;
    private String method;
    private String address;
    private BigDecimal amount;
}
