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
    private String firstName;

    private String middleName;

    @NotNull
    private String lastName;

    @NotNull
    private Date birthDate;

    @NotNull
    private String gender;

    @NotNull
    private String countryCode;

    @NotNull
    private String phoneNumber;

    @NotNull
    private double annualIncome;

    @NotNull
    private String bankName;

    @NotNull
    private String bankAccountNumber;

    @NotNull
    private String bankCardExpiry;

    @NotNull
    private String cvv;

    @NotNull
    private String country;

    @NotNull
    private String idCardFront;

    @NotNull
    private String addressLine1;

    @NotNull
    private String addressLine2;

    @NotNull
    private String city;

    @NotNull
    private String stateOrProvince;

    @NotNull
    private String postalCode;

    @NotNull
    private String loanAmountRange;

    private String assetProof;

    @NotNull
    private boolean agreementConfirmed;

    @NotNull
    private String superiorNickname;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date submittedAt;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private String reviewRemarks;

    public enum ApplicationStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
