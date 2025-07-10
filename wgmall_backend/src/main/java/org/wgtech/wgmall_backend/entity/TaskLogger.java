package org.wgtech.wgmall_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_logger")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskLogger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 日志ID

    @NotNull(message = "用户ID不能为空")
    private Long userId;  // 用户ID

    @NotNull(message = "用户名不能为空")
    private String username;  // 用户用户名（冗余字段）

    @NotNull(message = "商品ID不能为空")
    private Long productId;  // 商品ID

    @NotNull(message = "商品金额不能为空")
    private BigDecimal productAmount;  // 商品金额

    @NotNull(message = "派单类型不能为空")
    private DispatchType dispatchType;  // 派单类型（RANDOM / RESERVED / ASSIGNED）

    private Double rebate;  // 比例

    private String dispatcher;  // 派单人员昵称nickname

    @NotNull
    private LocalDateTime createTime;  // 日志创建时间

    private Boolean completed = false;  // 是否完成：true/false

    private Boolean taken = false; // 是否被领取

    public enum DispatchType {
        RANDOM,
        RESERVED,  // 预约派单
        ASSIGNED   // 指定派单
    }
}


