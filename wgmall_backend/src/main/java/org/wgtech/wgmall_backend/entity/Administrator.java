package org.wgtech.wgmall_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Administrator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 自动生成ID
    private int id;

    @Enumerated(EnumType.STRING)  // 枚举类型映射到数据库中的字符串
    private Role role;  // 业务员SALES、主管MANAGER、管理员ADMIN

    @Column(nullable = false, unique = true)
    private String username;  // 账号

    @Column(nullable = false, unique = true)
    private String nickname; // 昵称

    @Column(nullable = false)
    private String password;  // 密码

    @Column(nullable = true)
    private String inviteCode;  // 邀请码

    @OneToMany(mappedBy = "superior")  // 一对多关系，一个管理员可以有多个下级客户
    private List<User> subordinates;   // 下级客户列表

    @Column(nullable = false)
    private boolean isBanned;  // 是否被封禁 false是没被封号，true是封号

    public enum Role {
        SALES,    // 业务员
        MANAGER,  // 主管
        ADMIN     // 管理员
    }
}
