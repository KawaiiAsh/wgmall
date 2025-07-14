package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wgtech.wgmall_backend.entity.LoanApplication;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.repository.UserRepository;
import org.wgtech.wgmall_backend.service.LoanApplicationService;
import org.wgtech.wgmall_backend.utils.Result;

@RestController
@RequestMapping("/loan")
@Tag(name = "贷款接口") // Swagger 文档标签
public class LoanApplicationController {

    @Autowired
    private LoanApplicationService loanApplicationService;

    @Autowired
    private UserRepository userRepository;

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
            // 获取用户信息
            User user = userRepository.findById(loanApplication.getUser().getId())
                    .orElseThrow(() -> new IllegalArgumentException("用户未找到"));

            // 获取上级用户的昵称
            String superiorNickname = user.getSuperiorUsername();
            loanApplication.setSuperiorNickname(superiorNickname != null ? superiorNickname : "无上级");

            // 设置用户对象
            loanApplication.setUser(user);

            // 提交贷款申请
            LoanApplication submittedLoan = loanApplicationService.submitLoanApplication(loanApplication);

            // 返回结果
            return Result.success(submittedLoan);
        } catch (Exception e) {
            return Result.failure("贷款申请失败: " + e.getMessage());
        }
    }


}
