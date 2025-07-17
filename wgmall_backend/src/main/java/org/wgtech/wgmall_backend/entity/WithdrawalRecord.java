package org.wgtech.wgmall_backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date withdrawalTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String method;   // 提款方式，如 TRON、BTC、ETH、Bank 等
    private String address;  // 提款地址
    private String username; // 记录申请人名称

    private String remark;   // 备注：手动/自动/拒绝理由等

    private String status;   // 状态：PENDING, APPROVED, REJECTED

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date reviewTime; // 审核时间
}
