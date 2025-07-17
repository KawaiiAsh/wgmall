package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.wgtech.wgmall_backend.dto.LoanReviewRequest;
import org.wgtech.wgmall_backend.dto.RepayRequest;
import org.wgtech.wgmall_backend.entity.LoanApplication;
import org.wgtech.wgmall_backend.entity.RepaymentLog;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.repository.RepaymentLogRepository;
import org.wgtech.wgmall_backend.repository.UserRepository;
import org.wgtech.wgmall_backend.service.LoanApplicationService;
import org.wgtech.wgmall_backend.utils.Result;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    @PostMapping(value = "/apply", consumes = {"multipart/form-data"})
    @Operation(summary = "提交贷款表单（用户）")
    public Result<LoanApplication> applyLoan(
            @RequestPart("loanData") LoanApplication loanApplication,
            @RequestPart("idCardFront") MultipartFile idCardFront
    ) {
        try {
            User user = userRepository.findById(loanApplication.getUser().getId())
                    .orElseThrow(() -> new IllegalArgumentException("用户未找到"));

            String superiorNickname = user.getSuperiorUsername();
            loanApplication.setSuperiorNickname(superiorNickname != null ? superiorNickname : "无上级");

            // 保存身份证图片
            String uploadDir = "uploads/loan/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String filename = System.currentTimeMillis() + "_" + idCardFront.getOriginalFilename();
            Path filepath = Paths.get(uploadDir, filename);
            Files.write(filepath, idCardFront.getBytes());

            loanApplication.setIdCardFront("/" + filepath.toString().replace("\\", "/")); // 设置相对路径
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
    public Result<String> reviewLoan(@RequestBody LoanReviewRequest request) {
        LoanApplication loan = loanApplicationService.findById(request.getLoanId())
                .orElseThrow(() -> new IllegalArgumentException("贷款申请未找到"));

        if (!loan.getStatus().equals(LoanApplication.ApplicationStatus.PENDING)) {
            return Result.failure("该申请已审核，不能重复处理");
        }

        String decision = request.getDecision();
        if ("APPROVE".equalsIgnoreCase(decision)) {
            loan.setStatus(LoanApplication.ApplicationStatus.APPROVED);
        } else if ("REJECT".equalsIgnoreCase(decision)) {
            loan.setStatus(LoanApplication.ApplicationStatus.REJECTED);
        } else {
            return Result.failure("无效的审核决策");
        }

        loan.setReviewRemarks(request.getRemarks());
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
    public Result<String> repayLoan(@RequestBody RepayRequest request) {
        try {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("用户未找到"));

            if (user.getDebtAmount() == null || user.getDebtAmount().compareTo(BigDecimal.ZERO) <= 0) {
                return Result.failure("用户当前无欠款");
            }

            if (request.getAmount().compareTo(user.getDebtAmount()) > 0) {
                return Result.failure("还款金额不能大于欠款金额");
            }

            BigDecimal updatedDebt = user.getDebtAmount().subtract(request.getAmount());
            user.setDebtAmount(updatedDebt);
            userRepository.save(user);

            RepaymentLog log = RepaymentLog.builder()
                    .user(user)
                    .amount(request.getAmount())
                    .repaymentTime(new Date())
                    .operatedBy("用户自助")
                    .remarks("用户主动还款")
                    .build();
            repaymentLogRepository.save(log);

            return Result.success("还款成功，剩余欠款：" + updatedDebt);
        } catch (Exception e) {
            return Result.failure("还款失败: " + e.getMessage());
        }
    }



}
