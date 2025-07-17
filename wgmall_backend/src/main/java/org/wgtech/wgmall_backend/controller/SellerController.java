package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.wgtech.wgmall_backend.dto.ListingRequest;
import org.wgtech.wgmall_backend.dto.PayRequestDTO;
import org.wgtech.wgmall_backend.entity.*;
import org.wgtech.wgmall_backend.repository.*;
import org.wgtech.wgmall_backend.utils.Result;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/seller")
@RequiredArgsConstructor
@Tag(name = "卖家接口", description = "卖家管理店铺、商品、进货的相关接口")
public class SellerController {

    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final ListedProductRepository listedProductRepository;

    /**
     * 获取我的店铺信息
     */
    @Operation(summary = "获取我的店铺信息", description = "根据用户名返回对应卖家的店铺信息。用户必须为卖家身份。")
    @GetMapping("/me/shop")
    public Result<Shop> getMyShop(@RequestParam String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (user.getBuyerOrSaler() != 1) {
            return Result.failure("你还不是卖家");
        }
        Shop shop = shopRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new RuntimeException("未找到店铺"));
        return Result.success(shop);
    }

    /**
     * 获取可上架的商品（根据主营分类）
     */
    @Operation(summary = "获取可上架商品", description = "根据卖家店铺的主营类型，分页获取平台中可上架的商品列表。")
    @GetMapping("/me/shop/candidates")
    public Result<List<Product>> getCandidateProducts(@RequestParam String username,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Shop shop = shopRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new RuntimeException("未找到店铺"));

        List<Product.ProductType> types = shop.getMainProductTypes();
        Page<Product> productPage = productRepository.findByTypeIn(types, PageRequest.of(page, size));

        Result.Pagination pagination = new Result.Pagination(
                productPage.getNumber(),
                productPage.getTotalPages(),
                productPage.getTotalElements(),
                productPage.getSize()
        );
        return Result.success(productPage.getContent(), pagination);
    }

    /**
     * 批量上架商品
     */
    @Operation(summary = "批量上架商品", description = "将多个商品以指定价格上架到卖家的店铺中。需提供商品 ID 和售价。")
    @PostMapping("/me/shop/list")
    public Result<String> listProducts(@RequestBody ListingRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Shop shop = shopRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new RuntimeException("未找到店铺"));

        long currentCount = listedProductRepository.countByShopId(shop.getId());
        int newCount = req.getProductIds().size();

        if (currentCount + newCount > shop.getMaxListings()) {
            return Result.failure("超过上架限制数量（最多允许 " + shop.getMaxListings() + " 个）");
        }

        Map<Long, BigDecimal> priceMap = req.getSalePrices();

        List<Product> products = productRepository.findAllById(req.getProductIds());

        List<ListedProduct> listed = products.stream().map(p -> {
            if (!priceMap.containsKey(p.getId())) {
                throw new RuntimeException("缺少商品 " + p.getName() + " 的上架价格");
            }
            return ListedProduct.builder()
                    .shop(shop)
                    .product(p)
                    .salePrice(priceMap.get(p.getId()))
                    .listedAt(new Date())
                    .build();
        }).toList();

        listedProductRepository.saveAll(listed);

        return Result.success("成功上架 " + listed.size() + " 个商品");
    }

    private final PurchaseRequestRepository purchaseRequestRepository;

    @Operation(summary = "支付进货请求", description = "卖家为待付款的进货请求付款。系统将校验余额是否足够并更新状态。")
    @PostMapping("/purchase-requests/pay")
    @Transactional
    public Result<String> payForPurchaseRequests(@RequestBody PayRequestDTO dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (user.getBuyerOrSaler() != 1) {
            return Result.failure("你还不是卖家");
        }

        List<PurchaseRequest> requests = purchaseRequestRepository.findAllById(dto.getRequestIds());

        // 校验是否都属于该卖家
        for (PurchaseRequest req : requests) {
            if (!req.getBuyer().getId().equals(user.getId())) {
                return Result.failure("包含非你账户的请求");
            }
            if (req.getStatus() != PurchaseRequest.Status.PENDING) {
                return Result.failure("包含非待付款状态的请求");
            }
        }

        // 总进货价
        BigDecimal totalCost = requests.stream()
                .map(PurchaseRequest::getTotalWholesale)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 校验余额
        if (user.getBalance().compareTo(totalCost) < 0) {
            return Result.failure("余额不足，总需支付：" + totalCost + "，当前余额：" + user.getBalance());
        }

        // 扣余额，更新状态
        user.setBalance(user.getBalance().subtract(totalCost));
        for (PurchaseRequest req : requests) {
            req.setStatus(PurchaseRequest.Status.PROCESSING);
        }

        // 保存
        userRepository.save(user);
        purchaseRequestRepository.saveAll(requests);

        return Result.success("付款成功，进货处理中，总支付：" + totalCost);
    }
}


