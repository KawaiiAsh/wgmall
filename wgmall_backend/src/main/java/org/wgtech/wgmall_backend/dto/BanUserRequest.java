package org.wgtech.wgmall_backend.dto;


import lombok.Data;

@Data
public class BanUserRequest {
    private Long userId;
    private Boolean banned; // true 表示封禁，false 表示解封
}
