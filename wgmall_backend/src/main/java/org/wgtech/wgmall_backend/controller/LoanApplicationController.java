package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.wgtech.wgmall_backend.entity.LoanApplication;
import org.wgtech.wgmall_backend.entity.RepaymentLog;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.repository.RepaymentLogRepository;
import org.wgtech.wgmall_backend.repository.UserRepository;
import org.wgtech.wgmall_backend.service.LoanApplicationService;
import org.wgtech.wgmall_backend.utils.Result;

import java.math.BigDecimal;
import java.util.Date;

@RestController
@RequestMapping("/loan")
@Tag(name = "贷款接口") // Swagger 文档标签
public class LoanApplicationController {

    @Autowired
    private LoanApplicationService loanApplicationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RepaymentLogRepository repaymentLogRepository;
    /**
     * 提交贷款申请
     *
     * @param loanApplication 贷款申请的所有字段
     * @return 贷款申请提交结果
     */
    @PostMapping("/apply")
    @Operation(summary = "提交贷款表单（用户）")
    public Result<LoanApplication> applyLoan(@RequestBody LoanApplication loanApplication) {
        try {
            User user = userRepository.findById(loanApplication.getUser().getId())
                    .orElseThrow(() -> new IllegalArgumentException("用户未找到"));

            String superiorNickname = user.getSuperiorUsername();
            loanApplication.setSuperiorNickname(superiorNickname != null ? superiorNickname : "无上级");

            loanApplication.setUser(user);
            loanApplication.setStatus(LoanApplication.ApplicationStatus.PENDING);
            loanApplication.setSubmittedAt(new Date());

            LoanApplication submittedLoan = loanApplicationService.submitLoanApplication(loanApplication);

            return Result.success(submittedLoan);
        } catch (Exception e) {
            return Result.failure("贷款申请失败: " + e.getMessage());
        }
    }

    @GetMapping("/admin/list")
    @Operation(summary = "后台：获取贷款申请列表（分页、倒序）（客服）")
    public Result<Page<LoanApplication>> listLoanApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "submittedAt"));
        Page<LoanApplication> applications = loanApplicationService.findAll(pageable);
        return Result.success(applications);
    }

    @PostMapping("/admin/review")
    @Operation(summary = "后台：审核贷款申请（客服）")
    public Result<String> reviewLoan(
            @RequestParam Long loanId,
            @RequestParam String decision,
            @RequestParam(required = false) String remarks) {

        LoanApplication loan = loanApplicationService.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("贷款申请未找到"));

        if (!loan.getStatus().equals(LoanApplication.ApplicationStatus.PENDING)) {
            return Result.failure("该申请已审核，不能重复处理");
        }

        if ("APPROVE".equalsIgnoreCase(decision)) {
            loan.setStatus(LoanApplication.ApplicationStatus.APPROVED);
        } else if ("REJECT".equalsIgnoreCase(decision)) {
            loan.setStatus(LoanApplication.ApplicationStatus.REJECTED);
        } else {
            return Result.failure("无效的审核决策");
        }

        loan.setReviewRemarks(remarks);
        loanApplicationService.save(loan);
        return Result.success("审核成功");
    }

    /**
     * 用户还款
     *
     * @param userId 用户ID
     * @param amount 还款金额
     * @return 还款结果
     */
    @PostMapping("/repay")
    @Operation(summary = "用户还款(用户）")
    public Result<String> repayLoan(@RequestParam Long userId, @RequestParam BigDecimal amount) {
        try {
            // 获取用户信息
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("用户未找到"));

            // 检查是否有欠款
            if (user.getDebtAmount() == null || user.getDebtAmount().compareTo(BigDecimal.ZERO) <= 0) {
                return Result.failure("用户当前无欠款");
            }

            // 检查还款金额是否大于欠款
            if (amount.compareTo(user.getDebtAmount()) > 0) {
                return Result.failure("还款金额不能大于欠款金额");
            }

            // 执行还款逻辑
            BigDecimal updatedDebt = user.getDebtAmount().subtract(amount);
            user.setDebtAmount(updatedDebt);

            // 可选：记录还款流水或变动日志

            // 保存更新
            userRepository.save(user);

            RepaymentLog log = RepaymentLog.builder()
                    .user(user)
                    .amount(amount)
                    .repaymentTime(new Date())
                    .operatedBy("用户自助") // 或从 token 获取当前登录用户名
                    .remarks("用户主动还款")
                    .build();

            repaymentLogRepository.save(log);

            return Result.success("还款成功，剩余欠款：" + updatedDebt);
        } catch (Exception e) {
            return Result.failure("还款失败: " + e.getMessage());
        }
    }


}
