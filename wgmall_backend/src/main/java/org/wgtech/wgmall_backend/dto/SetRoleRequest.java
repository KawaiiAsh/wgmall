package org.wgtech.wgmall_backend.dto;

import lombok.Data;

@Data
public class SetRoleRequest {
    private Long userId;
    private int buyerOrSaler; // 1 = 买家，2 = 卖家
}