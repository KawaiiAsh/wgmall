package org.wgtech.wgmall_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wgtech.wgmall_backend.dto.SellerApplicationRequest;
import org.wgtech.wgmall_backend.entity.SellerApplication;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.repository.SellerApplicationRepository;
import org.wgtech.wgmall_backend.repository.UserRepository;

import java.util.Date;

@RestController
@RequestMapping("/seller-application")
@RequiredArgsConstructor
public class SellerApplicationController {

    private final SellerApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> apply(@RequestParam String username,
                                   @RequestBody SellerApplicationRequest request) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (applicationRepository.existsByUserId(user.getId())) {
            return ResponseEntity.badRequest().body("你已经提交过申请");
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
        return ResponseEntity.ok("申请已提交");
    }
}
