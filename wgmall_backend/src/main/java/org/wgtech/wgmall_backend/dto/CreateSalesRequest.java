package org.wgtech.wgmall_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSalesRequest {
    private String username;
    private String nickname;
    private String password;

    // 记得加 getter/setter 或用 Lombok 的 @Data 注解
}