package org.wgtech.wgmall_backend.dto;

import lombok.Data;

@Data
public class SetCanWithdrawRequest {
    private Long userId;
    private boolean canWithdraw;
}
