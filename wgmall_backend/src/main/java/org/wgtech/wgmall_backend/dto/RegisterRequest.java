package org.wgtech.wgmall_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String username;
    private String phone;
    private String password;
    private String invitecode;
    private String fundpassword;
    private String ip;

    // ✅ 一定要写 getter/setter（Lombok @Data 也可以）
    // 省略 getter/setter 示例...
}
