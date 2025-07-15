package org.wgtech.wgmall_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "seller_application")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String shopName;

    @Column(length = 1000)
    private String shopDescription;

    @Column(nullable = false)
    private String businessPhone;

    @Column(nullable = false)
    private String profession;

    @Column(nullable = false)
    private BigDecimal monthlyIncome;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<Product.ProductType> mainProductTypes;

    private String idFrontImage; // 路径
    private String idBackImage; // 路径

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status; // PENDING, APPROVED, REJECTED

    private Date applyTime;

    public enum ApplicationStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
