package org.wgtech.wgmall_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String province;
    private String city;
    private String district;
    private String street;
    private String detail;     // 详细地址，例如门牌号
    private String country; // 国家

    private String receiverName;
    private String receiverPhone;

    @OneToOne(mappedBy = "address") // 被维护方
    private User user;
}
