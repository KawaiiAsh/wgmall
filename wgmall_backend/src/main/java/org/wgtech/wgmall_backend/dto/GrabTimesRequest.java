package org.wgtech.wgmall_backend.dto;

import lombok.Data;

@Data
public class GrabTimesRequest {
    private Long userId;
    private int times;
}