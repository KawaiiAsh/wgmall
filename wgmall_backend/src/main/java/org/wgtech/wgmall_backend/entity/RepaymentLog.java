package org.wgtech.wgmall_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepaymentLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 还款用户
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 还款金额
    @Column(nullable = false)
    private BigDecimal amount;

    // 还款时间
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date repaymentTime;

    // 操作人（如管理员ID或用户名），可选
    private String operatedBy;

    // 备注（比如“用户自助还款”或“系统自动还款”）
    private String remarks;
}
