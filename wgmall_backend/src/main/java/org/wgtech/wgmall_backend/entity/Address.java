package org.wgtech.wgmall_backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 用户收货地址实体类。
 * 映射到数据库中的 address 表，用于存储用户的收货信息。
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    /**
     * 主键，自增长
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 省份名称，例如：广东省
     */
    private String province;

    /**
     * 城市名称，例如：广州市
     */
    private String city;

    /**
     * 区县名称，例如：天河区
     */
    private String district;

    /**
     * 街道名称，例如：体育东路
     */
    private String street;

    /**
     * 详细地址，例如门牌号、公寓楼层等
     */
    private String detail;

    /**
     * 国家，例如：中国、美国等
     */
    private String country;

    /**
     * 收件人姓名
     */
    private String receiverName;

    /**
     * 收件人手机号
     */
    private String receiverPhone;

    /**
     * 与用户表的一对一关联，由 User 表维护关系。
     * mappedBy = "address" 表示这个关系由 User 中的 address 字段维护。
     */
    @OneToOne(mappedBy = "address")
    private User user;
}
