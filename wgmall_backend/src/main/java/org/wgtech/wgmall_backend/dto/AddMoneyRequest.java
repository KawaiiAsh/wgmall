package org.wgtech.wgmall_backend.dto;

import lombok.Data;

@Data
public class AddMoneyRequest {
    private Long userId;
    private double amount;
}
