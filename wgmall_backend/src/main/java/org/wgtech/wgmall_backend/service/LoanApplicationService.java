package org.wgtech.wgmall_backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.wgtech.wgmall_backend.entity.LoanApplication;

import java.util.Optional;

public interface LoanApplicationService {
    LoanApplication submitLoanApplication(LoanApplication loanApplication);
    Optional<LoanApplication> findById(Long id);
    Page<LoanApplication> findAll(Pageable pageable);
    LoanApplication save(LoanApplication loanApplication);
}
