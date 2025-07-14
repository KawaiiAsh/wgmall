package org.wgtech.wgmall_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)  // 使用联合继承策略
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 自动生成ID
    private Long id;

    @NotNull(message = "用户名不能为空")  // 非空校验
    private String username;          // 用户名

    @NotNull(message = "密码不能为空")  // 非空校验
    private String password;          // 密码

    private String nickname;            // 昵称

    @NotNull(message = "电话不能为空")  // 非空校验
    private String phone;             // 电话

    @NotNull(message = "IP不能为空")  // 非空校验
    private String ip;                // IP

    @NotNull(message = "余额不能为空")  // 非空校验
    private BigDecimal balance;           // 余额

    @NotNull(message = "不可用余额不能为0.00")
    private BigDecimal  noneUsefulBalance; // 不可用余额

    @NotNull(message = "邀请码不能为空")  // 非空校验
    private String inviteCode;        // 邀码

    // 用字符串存储上级昵称（可能是用户，也可能是管理员）
    private String superiorUsername; // 其实是上级的nickname

    @NotNull(message = "接单数量不能为空")  // 非空校验
    private int orderCount;           // 可刷单数量

    @NotNull(message = "总刷单次数")
    private int totalOrderCount;

    private String remarks;           // 备注（可以为空）

    @NotNull(message = "注册时间不能为空")  // 非空校验
    private Date registerTime;        // 注册时间

    @NotNull(message = "上次登录时间不能为空")  // 非空校验
    private Date lastLoginTime;       // 上次登录时间

    @NotNull(message = "在线状态不能为空")  // 非空校验
    private boolean isOnline;         // 是否在线

    @NotNull(message = "封禁状态不能为空")  // 非空校验
    private boolean isBanned;         // 是否被封禁

    @NotNull(message = "资金密码不能为空") // 资金密码
    private String fundPassword;

    @NotNull(message = "是否ip重复") // 重复ip，0不是重复，1是重复
    private int repeatIp;

    @NotNull(message = "是否可以抢单") // 默认是false，手动开启可以抢是true
    private boolean toggle;

    @NotNull(message = "是否可以提款") // 默认是false，手动开启可以是true
    private boolean canWithdraw;

    @NotNull(message = "是否处于预约派单状态")
    private boolean appointmentStatus;          // 是否处于指定预约派单状态,默认是false,手动开启是true

    private Integer appointmentNumber;         // 如果是NULL，不然就是用户的刷单次数还剩多少，appointmentNumber就是预约派单的。

    @NotNull(message = "返点")
    private double rebate;              // 一级返点是0.006，二级返点是0.02，三级返点是0.06。

    private BigDecimal totalProfit; // 用户累计获得的利润

    @NotNull(message = "买家还是卖家")
    private int buyerOrSaler; // 身份 0买家，1是卖家

    // 收款信息
    private String tronWalletAddress;
    private String bitCoinWalletAddress;
    private String ethWalletAddress;
    private String coinWalletAddress;

//    @Size(min = 1, max = 50, message = "银行卡名称长度在1到50个字符之间")
//    private String bankName;          // 银行卡名称（可以为空）
//
//    @Size(min = 16, max = 19, message = "银行卡账号长度应为16到19个字符")
//    private String bankAccount;       // 银行卡账号（可以为空）
//
//    private Date bankExpiryDate;      // 银行卡有效期（可以为空）
//
//    @Size(min = 3, max = 3, message = "银行卡后三位应该是3个数字")
//    private String bankCardCVV;       // 银行卡后三位（可以为空）

    private String country;           // 国家

    // 活动属性
    @NotNull(message = "用户的抽奖次数")
    private int redBagDrawCount;        // 用户的红包抽取次数

    @NotNull(message = "用户的总抽奖数或者第几天")
    private int redBagCount;        // 红包领取次数or第几天

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Wishlist> wishlists;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private LoanApplication loanApplication;  // 用户的贷款申请

}
