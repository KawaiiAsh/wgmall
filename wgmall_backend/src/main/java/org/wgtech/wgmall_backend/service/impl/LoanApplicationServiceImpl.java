package org.wgtech.wgmall_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.wgtech.wgmall_backend.entity.LoanApplication;
import org.wgtech.wgmall_backend.repository.LoanApplicationRepository;
import org.wgtech.wgmall_backend.service.LoanApplicationService;

import java.util.Optional;

@Service
public class LoanApplicationServiceImpl implements LoanApplicationService {

    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    @Override
    public LoanApplication submitLoanApplication(LoanApplication loanApplication) {
        return loanApplicationRepository.save(loanApplication);
    }

    @Override
    public Optional<LoanApplication> findById(Long id) {
        return loanApplicationRepository.findById(id);
    }

    @Override
    public Page<LoanApplication> findAll(Pageable pageable) {
        return loanApplicationRepository.findAll(pageable);
    }

    @Override
    public LoanApplication save(LoanApplication loanApplication) {
        return loanApplicationRepository.save(loanApplication);
    }
}
