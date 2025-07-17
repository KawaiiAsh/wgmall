package org.wgtech.wgmall_backend.dto;

import lombok.Data;

@Data
public class SetRebateRequest {
    private Long userId;
    private double rebate;
}