package org.wgtech.wgmall_backend.dto;

import lombok.Data;

@Data
public class GrabToggleRequest {
    private Long userId;
    private boolean toggle;
}
