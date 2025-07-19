package org.wgtech.wgmall_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "用户实体")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "主键ID，自动生成")
    private Long id;

    @NotNull(message = "用户名不能为空")
    @Schema(description = "用户名，不能为空")
    private String username;

    @NotNull(message = "密码不能为空")
    @Schema(description = "用户密码，不能为空")
    private String password;

    @Schema(description = "用户昵称，可为空")
    private String nickname;

    @NotNull(message = "电话不能为空")
    @Schema(description = "手机号，不能为空")
    private String phone;

    @NotNull(message = "IP不能为空")
    @Schema(description = "用户注册或登录IP，不能为空")
    private String ip;

    @NotNull(message = "余额不能为空")
    @Schema(description = "账户余额，不能为空，单位：元")
    private BigDecimal balance;

    @NotNull(message = "不可用余额不能为空")
    @Schema(description = "不可用余额（提现冻结等），不能为空")
    private BigDecimal noneUsefulBalance;

    @NotNull(message = "欠款金额不能为空")
    @Schema(description = "欠款金额，不能为空")
    private BigDecimal debtAmount;

    @NotNull(message = "邀请码不能为空")
    @Schema(description = "邀请码，不能为空")
    private String inviteCode;

    @Schema(description = "上级昵称，可能是管理员或用户")
    private String superiorUsername;

    @NotNull(message = "接单数量不能为空")
    @Schema(description = "接单数量，不能为空")
    private Integer orderCount;

    @NotNull(message = "总刷单次数不能为空")
    @Schema(description = "刷单总次数")
    private int totalOrderCount;

    @Schema(description = "备注信息，可为空")
    private String remarks;

    @NotNull(message = "注册时间不能为空")
    @Schema(description = "注册时间")
    private Date registerTime;

    @NotNull(message = "上次登录时间不能为空")
    @Schema(description = "上次登录时间")
    private Date lastLoginTime;

    @NotNull(message = "在线状态不能为空")
    @Schema(description = "是否在线")
    private boolean isOnline;

    @NotNull(message = "封禁状态不能为空")
    @Schema(description = "是否被封禁")
    private boolean banned;

    @NotNull(message = "资金密码不能为空")
    @Schema(description = "资金密码，不能为空")
    private String fundPassword;

    @NotNull(message = "是否ip重复")
    @Schema(description = "是否为重复IP（0不是，1是）")
    private int repeatIp;

    @NotNull(message = "是否可以抢单")
    @Schema(description = "是否允许抢单")
    private boolean toggle;

    @NotNull(message = "是否可以提款")
    @Schema(description = "是否允许提款")
    private boolean canWithdraw;

    @NotNull(message = "是否处于预约派单状态")
    @Schema(description = "是否处于预约派单状态")
    private boolean appointmentStatus;

    @Schema(description = "剩余预约派单次数，可为空")
    private Integer appointmentNumber;

    @NotNull(message = "返点不能为空")
    @Schema(description = "返点比例，例如 0.02 表示 2%")
    private double rebate;

    @Schema(description = "累计获得利润")
    private BigDecimal totalProfit;

    @NotNull(message = "买家还是卖家")
    @Schema(description = "身份：0买家，1卖家")
    private int buyerOrSaler;

    @Schema(description = "TRON 钱包地址，可用于收款")
    private String tronWalletAddress;

    @Schema(description = "比特币钱包地址")
    private String bitCoinWalletAddress;

    @Schema(description = "以太坊钱包地址")
    private String ethWalletAddress;

    @Schema(description = "其他币种钱包地址")
    private String coinWalletAddress;

    @Schema(description = "国家，可为空")
    private String country;

    @NotNull(message = "用户的抽奖次数")
    @Schema(description = "红包抽奖次数")
    private int redBagDrawCount;

    @NotNull(message = "用户的总抽奖数或者第几天")
    @Schema(description = "红包总抽奖次数或当前天数")
    private int redBagCount;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    @Schema(description = "用户地址信息")
    private Address address;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "用户收藏的商品（心愿单）")
    private List<Wishlist> wishlists;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @Schema(description = "贷款申请记录")
    private LoanApplication loanApplication;

    @NotNull(message = "是否有未读消息")
    @Schema(description = "是否有未读站内信")
    private boolean hasUnreadMessages;

}
