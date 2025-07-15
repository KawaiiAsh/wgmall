package org.wgtech.wgmall_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "productId", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Product product;

    @NotNull(message = "商品金额不能为空")
    private BigDecimal productAmount;

    @NotNull(message = "派单类型不能为空")
    @Enumerated(EnumType.STRING)
    private DispatchType dispatchType;

    private Double rebate;

    private String dispatcher;

    @NotNull
    private LocalDateTime createTime;

    private LocalDateTime completeTime;

    private Boolean completed = false;

    private Boolean taken = false;
}
