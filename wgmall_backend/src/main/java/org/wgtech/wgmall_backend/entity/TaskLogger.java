package org.wgtech.wgmall_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_logger")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskLogger {

    public enum DispatchType {
        RANDOM,
        RESERVED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "用户名不能为空")
    private String username;

    // ✅ 商品信息快照字段
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @NotNull(message = "商品名称不能为空")
    private String productName;

    @NotNull(message = "商品金额不能为空")
    private BigDecimal productAmount;

    @NotNull(message = "商品图片路径不能为空")
    private String productImagePath;

    @NotNull(message = "派单类型不能为空")
    @Enumerated(EnumType.STRING)
    private DispatchType dispatchType;

    private Double rebate;

    private String dispatcher;

    @NotNull
    private LocalDateTime createTime;

    private LocalDateTime completeTime;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(nullable = false)
    private boolean taken = false;

}
