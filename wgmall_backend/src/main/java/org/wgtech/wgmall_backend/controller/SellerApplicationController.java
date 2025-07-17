package org.wgtech.wgmall_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.wgtech.wgmall_backend.dto.SellerApplicationRequest;
import org.wgtech.wgmall_backend.entity.SellerApplication;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.repository.SellerApplicationRepository;
import org.wgtech.wgmall_backend.repository.UserRepository;
import org.wgtech.wgmall_backend.utils.Result;

import java.util.Date;

@RestController
@RequestMapping("/seller-application")
@RequiredArgsConstructor
public class SellerApplicationController {

    private final SellerApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    @PostMapping("/apply")
    public Result<String> apply(@RequestBody SellerApplicationRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElse(null);

        if (user == null) {
            return Result.badRequest("用户不存在");
        }

        if (applicationRepository.existsByUserId(user.getId())) {
            return Result.badRequest("你已经提交过申请");
        }

        SellerApplication application = SellerApplication.builder()
                .user(user)
                .shopName(request.getShopName())
                .shopDescription(request.getShopDescription())
                .businessPhone(request.getBusinessPhone())
                .profession(request.getProfession())
                .monthlyIncome(request.getMonthlyIncome())
                .mainProductTypes(request.getMainProductTypes())
                .idFrontImage(request.getIdFrontImage())
                .idBackImage(request.getIdBackImage())
                .status(SellerApplication.ApplicationStatus.PENDING)
                .applyTime(new Date())
                .build();

        applicationRepository.save(application);
        return Result.success("申请已提交");
    }

}
