package org.wgtech.wgmall_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
    private double balance;           // 余额

    @NotNull(message = "邀请码不能为空")  // 非空校验
    private String inviteCode;        // 邀码

    // 用字符串存储上级昵称（可能是用户，也可能是管理员）
    private String superiorUsername;

    @NotNull(message = "接单数量不能为空")  // 非空校验
    private int orderCount;           // 可刷单数量

    private String remarks;           // 备注（可以为空）

    @NotNull(message = "注册时间不能为空")  // 非空校验
    private Date registerTime;        // 注册时间

    @NotNull(message = "上次登录时间不能为空")  // 非空校验
    private Date lastLoginTime;       // 上次登录时间

    @NotNull(message = "在线状态不能为空")  // 非空校验
    private boolean isOnline;         // 是否在线

    @NotNull(message = "封禁状态不能为空")  // 非空校验
    private boolean isBanned;         // 是否被封禁

    @NotNull(message = "等级不能为空")  // 非空校验
    @Builder.Default  // 设置默认值为1
    private int level = 1;             // 等级（默认值为1）

    @NotNull(message = "资金密码不能为空") // 资金密码
    private String fundPassword;

    @NotNull(message = "是否ip重复") // 重复ip，0不是重复，1是重复
    private int repeatIp;

    @NotNull(message = "是否可以抢单") // 默认是false，手动开启可以抢是true
    private boolean toggle;

    @NotNull(message = "是否可以提款") // 默认是false，手动开启可以是true
    private boolean canWithdraw;

    private boolean hasAssignedOrder;   // ✔️ 是否存在一个待领取的指派订单或者预约订单

    private int reservedIndex;          // ✔️ 倒数第几单触发预约订单（类型3，可选）

    // 银行卡信息
    @Size(min = 1, max = 50, message = "银行卡名称长度在1到50个字符之间")
    private String bankName;          // 银行卡名称（可以为空）

    @Size(min = 16, max = 19, message = "银行卡账号长度应为16到19个字符")
    private String bankAccount;       // 银行卡账号（可以为空）

    private Date bankExpiryDate;      // 银行卡有效期（可以为空）

    @Size(min = 3, max = 3, message = "银行卡后三位应该是3个数字")
    private String bankCardCVV;       // 银行卡后三位（可以为空）

    private String country;           // 国家

    // 计算用户的返点
    public double getRebate() {
        switch (this.level) {
            case 1:
                return 0.6;  // 一级返点 0.6%
            case 2:
                return 2.0;  // 二级返点 2%
            case 3:
                return 6.0;  // 三级返点 6%
            default:
                return 0.6;  // 默认返回0.6%
        }
    }
}
