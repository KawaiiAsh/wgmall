package org.wgtech.wgmall_backend.controller;

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

@RestController
@RequestMapping("/seller/shipping")
@RequiredArgsConstructor
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

    @GetMapping("/all")
    public Result<Page<ShippingItem>> getAll(@RequestParam String username,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        return fetchShipping(findSeller(username), null, page, size);
    }

    @GetMapping("/pending")
    public Result<Page<ShippingItem>> getPending(@RequestParam String username,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        return fetchShipping(findSeller(username), ShippingItem.ShippingStatus.PENDING, page, size);
    }

    @GetMapping("/processing")
    public Result<Page<ShippingItem>> getProcessing(@RequestParam String username,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        return fetchShipping(findSeller(username), ShippingItem.ShippingStatus.PROCESSING, page, size);
    }

    @GetMapping("/shipped")
    public Result<Page<ShippingItem>> getShipped(@RequestParam String username,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        return fetchShipping(findSeller(username), ShippingItem.ShippingStatus.SHIPPED, page, size);
    }

    @GetMapping("/warehouse")
    public Result<Page<ShippingItem>> getWarehouse(@RequestParam String username,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        return fetchShipping(findSeller(username), ShippingItem.ShippingStatus.WAREHOUSE, page, size);
    }

    @GetMapping("/transporting")
    public Result<Page<ShippingItem>> getTransporting(@RequestParam String username,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        return fetchShipping(findSeller(username), ShippingItem.ShippingStatus.TRANSPORTING, page, size);
    }

    @GetMapping("/delivered")
    public Result<Page<ShippingItem>> getDelivered(@RequestParam String username,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        return fetchShipping(findSeller(username), ShippingItem.ShippingStatus.DELIVERED, page, size);
    }

    @GetMapping("/completed")
    public Result<Page<ShippingItem>> getCompleted(@RequestParam String username,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        return fetchShipping(findSeller(username), ShippingItem.ShippingStatus.COMPLETED, page, size);
    }
}
