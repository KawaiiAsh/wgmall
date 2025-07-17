package org.wgtech.wgmall_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wgtech.wgmall_backend.entity.TaskLogger;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Long taskId;                     // 日志ID
    private String firstImagePath;           // 商品图片
    private String productName;             // 商品名称
    private Long productId;                 // 商品ID
    private BigDecimal productAmount;       // 商品金额
    private TaskLogger.DispatchType dispatchType;


    private BigDecimal expectReturn;  // 总返还
    private BigDecimal commission;    // 实际赚
}
