// src/main/java/org/wgtech/wgmall_backend/dto/AdminUserTaskQueryRequest.java
package org.wgtech.wgmall_backend.dto;

import lombok.Data;

@Data
public class AdminUserTaskQueryRequest {
    private String username;
    private Integer page;
    private Integer size;
}
