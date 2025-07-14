package org.wgtech.wgmall_backend.service;

import org.wgtech.wgmall_backend.entity.LoanApplication;

public interface LoanApplicationService {

    // 提交贷款申请
    LoanApplication submitLoanApplication(LoanApplication loanApplication);
}
