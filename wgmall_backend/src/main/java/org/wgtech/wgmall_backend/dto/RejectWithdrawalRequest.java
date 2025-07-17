package org.wgtech.wgmall_backend.dto;

import lombok.Data;

@Data
public class RejectWithdrawalRequest {
    private Long withdrawalId;
    private String reason;
}
