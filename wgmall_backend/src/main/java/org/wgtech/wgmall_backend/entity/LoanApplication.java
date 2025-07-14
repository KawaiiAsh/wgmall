package org.wgtech.wgmall_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String firstName;          // 名字

    private String middleName;         // 中间名（可选）

    @NotNull
    private String lastName;           // 姓

    @NotNull
    private Date birthDate;            // 出生日期

    @NotNull
    private String gender;             // 性别

    @NotNull
    private String countryCode;        // 国家区号

    @NotNull
    private String phoneNumber;        // 手机号

    @NotNull
    private double annualIncome;       // 年收入

    @NotNull
    private String bankName;           // 银行卡名称

    @NotNull
    private String bankAccountNumber;  // 银行卡卡号

    @NotNull
    private String bankCardExpiry;     // 截止日期 (MM/YY)

    @NotNull
    private String cvv;                // CVV

    @NotNull
    private String country;            // 国家

    @NotNull
    private String idCardFront;        // 身份证正面图片或护照正面图片路径

    @NotNull
    private String addressLine1;       // 地址1

    @NotNull
    private String addressLine2;       // 地址2

    @NotNull
    private String city;               // 城市

    @NotNull
    private String stateOrProvince;   // 省/州

    @NotNull
    private String postalCode;         // 邮政编码

    @NotNull
    private String loanAmountRange;    // 期望的贷款金额区间

    private String assetProof;         // 资产证明（可选，房产证，车证）

    @NotNull
    private boolean agreementConfirmed; // 确认协议

    @NotNull
    private String superiorNickname;           // 上级 nickname

    // 添加与 User 的一对一关系
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user; // 与 User 一对多关系
}
