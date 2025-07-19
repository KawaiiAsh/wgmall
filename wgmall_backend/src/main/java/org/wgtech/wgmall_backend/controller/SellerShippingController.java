package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import org.wgtech.wgmall_backend.entity.PurchaseItem;
import org.wgtech.wgmall_backend.entity.PurchaseRequest;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.repository.PurchaseItemRepository;
import org.wgtech.wgmall_backend.repository.UserRepository;
import org.wgtech.wgmall_backend.utils.Result;

@RestController
@RequestMapping("/seller/shipping")
@RequiredArgsConstructor
@Tag(name = "卖家发货接口", description = "卖家查询各类发货任务的接口")
public class SellerShippingController {

    private final UserRepository userRepository;
    private final PurchaseItemRepository purchaseItemRepository;

    private User findSeller(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (user.getBuyerOrSaler() != 1) {
            throw new RuntimeException("当前用户不是卖家");
        }
        return user;
    }

    /**
     * 核心查询逻辑
     */
    private Result<Page<PurchaseItem>> fetchShipping(User seller, PurchaseRequest.Status status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<PurchaseItem> result;

        if (status == null) {
            result = purchaseItemRepository.findByRequest_Buyer_Id(seller.getId(), pageable);
        } else {
            result = purchaseItemRepository.findByRequest_Buyer_IdAndRequest_Status(seller.getId(), status, pageable);
        }

        return Result.success(result);
    }

    @Operation(summary = "查询所有发货项", description = "按分页返回卖家所有的发货任务（不区分状态）")
    @GetMapping("/all")
    public Result<Page<PurchaseItem>> getAll(@RequestParam String username,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        return fetchShipping(findSeller(username), null, page, size);
    }

    @GetMapping("/pending")
    @Operation(summary = "查询待发货项", description = "返回所有处于 PENDING 状态的采购任务项")
    public Result<Page<PurchaseItem>> getPending(@RequestParam String username,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        return fetchShipping(findSeller(username), PurchaseRequest.Status.PENDING, page, size);
    }

    @GetMapping("/processing")
    @Operation(summary = "查询处理中发货项")
    public Result<Page<PurchaseItem>> getProcessing(@RequestParam String username,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        return fetchShipping(findSeller(username), PurchaseRequest.Status.PROCESSING, page, size);
    }

    @GetMapping("/shipped")
    @Operation(summary = "查询已发货项")
    public Result<Page<PurchaseItem>> getShipped(@RequestParam String username,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        return fetchShipping(findSeller(username), PurchaseRequest.Status.SHIPPED, page, size);
    }

    @GetMapping("/warehouse")
    @Operation(summary = "查询在仓库中的发货项")
    public Result<Page<PurchaseItem>> getWarehouse(@RequestParam String username,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        return fetchShipping(findSeller(username), PurchaseRequest.Status.WAREHOUSE, page, size);
    }

    @GetMapping("/transporting")
    @Operation(summary = "查询运输中的发货项")
    public Result<Page<PurchaseItem>> getTransporting(@RequestParam String username,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        return fetchShipping(findSeller(username), PurchaseRequest.Status.TRANSPORTING, page, size);
    }

    @GetMapping("/delivered")
    @Operation(summary = "查询已送达的发货项")
    public Result<Page<PurchaseItem>> getDelivered(@RequestParam String username,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        return fetchShipping(findSeller(username), PurchaseRequest.Status.DELIVERED, page, size);
    }

    @GetMapping("/completed")
    @Operation(summary = "查询已完成的发货项")
    public Result<Page<PurchaseItem>> getCompleted(@RequestParam String username,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        return fetchShipping(findSeller(username), PurchaseRequest.Status.COMPLETED, page, size);
    }
}
