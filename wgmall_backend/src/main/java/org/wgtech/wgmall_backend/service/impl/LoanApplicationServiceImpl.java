package org.wgtech.wgmall_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wgtech.wgmall_backend.entity.LoanApplication;
import org.wgtech.wgmall_backend.repository.LoanApplicationRepository;
import org.wgtech.wgmall_backend.service.LoanApplicationService;

@Service
public class LoanApplicationServiceImpl implements LoanApplicationService {

    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    @Override
    public LoanApplication submitLoanApplication(LoanApplication loanApplication) {
        // 将贷款申请保存到数据库
        return loanApplicationRepository.save(loanApplication);
    }
}