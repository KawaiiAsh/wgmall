package org.wgtech.wgmall_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReserveTaskRequest {
    private Long userId;
    private String username;
    private Long productId;
    private BigDecimal productAmount;
    private Double commissionRate;
    private String dispatcher;
    // Getter / Setter
}
