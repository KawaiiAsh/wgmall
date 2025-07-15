package org.wgtech.wgmall_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class PayRequestDTO {
    private String username;
    private List<Long> requestIds;
}
