package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wgtech.wgmall_backend.entity.ShippingItem;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.repository.ShippingItemRepository;
import org.wgtech.wgmall_backend.repository.UserRepository;
import org.wgtech.wgmall_backend.utils.Result;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/seller/shipping")
@RequiredArgsConstructor
@Tag(name = "卖家发货接口", description = "卖家查询各类发货任务的接口")
public class SellerShippingController {

    private final UserRepository userRepository;
    private final ShippingItemRepository shippingItemRepository;

    private User findSeller(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (user.getBuyerOrSaler() != 1) {
            throw new RuntimeException("当前用户不是卖家");
        }
        return user;
    }

    private Result<Page<ShippingItem>> fetchShipping(User user, ShippingItem.ShippingStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<ShippingItem> result = (status == null)
                ? shippingItemRepository.findByRequest_Buyer_Id(user.getId(), pageable)
                : shippingItemRepository.findByRequest_Buyer_IdAndStatus(user.getId(), status, pageable);
        return Result.success(result);
    }

    @Operation(summary = "查询所有发货项", description = "按分页返回卖家所有的发货任务（不区分状态）")
    @GetMapping("/all")
    public Result<Page<ShippingItem>> getAll(@RequestParam String username,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        return fetchShipping(findSeller(username), null, page, size);
    }
    @Operation(summary = "查询待发货项", description = "返回所有处于 PENDING 状态的发货任务")
    @GetMapping("/pending")
    public Result<Page<ShippingItem>> getPending(@RequestParam String username,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        return fetchShipping(findSeller(username), ShippingItem.ShippingStatus.PENDING, page, size);
    }

    @Operation(summary = "查询处理中发货项", description = "返回处于 PROCESSING 状态的发货任务")
    @GetMapping("/processing")
    public Result<Page<ShippingItem>> getProcessing(@RequestParam String username,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        return fetchShipping(findSeller(username), ShippingItem.ShippingStatus.PROCESSING, page, size);
    }

    @Operation(summary = "查询已发货项", description = "返回处于 SHIPPED 状态的发货任务")
    @GetMapping("/shipped")
    public Result<Page<ShippingItem>> getShipped(@RequestParam String username,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        return fetchShipping(findSeller(username), ShippingItem.ShippingStatus.SHIPPED, page, size);
    }

    @Operation(summary = "查询在仓库中的发货项", description = "返回处于 WAREHOUSE 状态的发货任务")
    @GetMapping("/warehouse")
    public Result<Page<ShippingItem>> getWarehouse(@RequestParam String username,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        return fetchShipping(findSeller(username), ShippingItem.ShippingStatus.WAREHOUSE, page, size);
    }

    @Operation(summary = "查询运输中的发货项", description = "返回处于 TRANSPORTING 状态的发货任务")
    @GetMapping("/transporting")
    public Result<Page<ShippingItem>> getTransporting(@RequestParam String username,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        return fetchShipping(findSeller(username), ShippingItem.ShippingStatus.TRANSPORTING, page, size);
    }

    @Operation(summary = "查询已送达的发货项", description = "返回处于 DELIVERED 状态的发货任务")
    @GetMapping("/delivered")
    public Result<Page<ShippingItem>> getDelivered(@RequestParam String username,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        return fetchShipping(findSeller(username), ShippingItem.ShippingStatus.DELIVERED, page, size);
    }

    @Operation(summary = "查询已完成的发货项", description = "返回处于 COMPLETED 状态的发货任务")
    @GetMapping("/completed")
    public Result<Page<ShippingItem>> getCompleted(@RequestParam String username,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        return fetchShipping(findSeller(username), ShippingItem.ShippingStatus.COMPLETED, page, size);
    }
}
