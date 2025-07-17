package org.wgtech.wgmall_backend.dto;

import lombok.Data;

@Data
public class GrabEligibilityRequest {
    private Long userId;
    private boolean eligible;
}
