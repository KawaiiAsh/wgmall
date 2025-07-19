package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.wgtech.wgmall_backend.dto.*;
import org.wgtech.wgmall_backend.entity.*;
import org.wgtech.wgmall_backend.repository.*;
import org.wgtech.wgmall_backend.service.AdministratorService;
import org.wgtech.wgmall_backend.service.UserService;
import org.wgtech.wgmall_backend.utils.SalespersonCreator;
import org.wgtech.wgmall_backend.utils.Result;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController // 声明这是一个 REST 控制器，返回值默认以 JSON 形式响应
@RequestMapping("/administrator") // 设置请求路径前缀为 /administrator
@Tag(name = "工作人员接口", description = "创建，封禁，获取所有业务员，客服的功能") // Swagger 文档标签
public class AdministratorController {

    private static final Logger log = LoggerFactory.getLogger(AdministratorController.class);

    @Autowired
    private SalespersonCreator salespersonCreator; // 工具类，用于创建业务员对象

    @Autowired
    private AdministratorService administratorService; // 管理员服务，用于查询业务员列表

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ListedProductRepository listedProductRepository;

    @Autowired
    private PurchaseRequestRepository purchaseRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WithdrawalRecordRepository withdrawalRecordRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AdministratorRepository administratorRepository;
    /**
     * 创建业务员账号
     *
     * 接口地址：POST /administrator/createsales
     * 管理员通过此接口创建业务员账号，传入用户名、昵称和密码。
     *
     * @return 操作结果，包含成功时返回的业务员信息或失败提示
     */
    @PostMapping("/createsales")
    @Operation(summary = "创建业务员账号（身份“BOSS”的权限）")
    public Result<Administrator> createSalesperson(
            @RequestBody CreateSalesRequest request
    ) {
        // 校验：确保用户名、密码、昵称都不为空
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            return Result.failure("用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return Result.failure("密码不能为空");
        }
        if (request.getNickname() == null || request.getNickname().isBlank()) {
            return Result.failure("昵称不能为空");
        }

        // 校验：检查用户名是否已存在
        if (administratorRepository.existsByUsername(request.getUsername())) {
            return Result.failure("用户名已被占用，请选择其他用户名");
        }

        // 校验：检查昵称是否已存在
        if (administratorRepository.existsByNickname(request.getNickname())) {
            return Result.failure("昵称已被占用，请选择其他昵称");
        }

        // 调用创建业务员的方法
        Administrator admin = salespersonCreator.createSalesperson(
                request.getUsername(),
                request.getNickname(),
                request.getPassword()
        );
        return Result.success(admin);
    }



    /**
     * 封禁管理员账号
     *
     * 接口地址：POST /administrator/ban/{id}
     * 管理员可以调用此接口来封禁指定的管理员账号
     *
     * @param id 管理员ID
     * @return 操作结果
     */
    @PutMapping("/sales/{id}/ban")
    @Operation(
            summary = "设置业务员封禁状态（BOSS权限）",
            description = "通过参数 isBanned 设置业务员是否被封禁"
    )
    public Result<Void> setSalesBanStatus(@PathVariable int id, @RequestParam boolean isBanned) {
        try {
            administratorService.setBanStatus(id, isBanned);
            return Result.success();
        } catch (Exception e) {
            return Result.failure("设置封禁状态失败：" + e.getMessage());
        }
    }


    @PostMapping("/unbansales/{id}")
    @Operation(summary = "解封业务员账号（身份“BOSS”的权限）", description = "将指定业务员账号的 isBanned 设置为 false")
    public Result<Void> unbanAdministrator(@PathVariable int id) {
        try {
            administratorService.unbanAdministrator(id);
            return Result.success();
        } catch (Exception e) {
            return Result.failure("解封失败：" + e.getMessage());
        }
    }

    /**
     * 查询所有管理员（BOSS）
     */
    @GetMapping("/bosses")
    @Operation(summary = "获取所有管理员（暂时用不上，不写）", description = "列出所有角色为BOSS的管理员账号")
    public Result<List<Administrator>> getAllBosses() {
        List<Administrator> bosses = administratorService.getAllBosses();
        return Result.success(bosses);
    }

    @GetMapping("/sales/page")
    @Operation(summary = "分页获取所有业务员（身份”BOSS“的权限）", description = "按ID倒序分页获取")
    public Result<Page<Administrator>> getSalesPageByIdDesc(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Administrator> result = administratorService.getAllSalesDesc(page, size);
        return Result.success(result);
    }


    @GetMapping("/sales/search")
    @Operation(summary = "根据昵称搜索业务员（身份”BOSS“的权限）", description = "模糊查询 + 分页 + 倒序")
    public Result<Page<Administrator>> searchSales(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Administrator> result = administratorService.searchSalesByNickname(keyword, page, size);
        return Result.success(result);
    }

    @GetMapping("/sales/{id:\\d+}")
    @Operation(summary = "根据ID查找业务员（身份”BOSS“的权限“）", description = "返回指定业务员信息")
    public Result<Administrator> findSalesById(@PathVariable long id) {
        return administratorService.findSalesById(id);
    }

    @PostMapping("/purchase-request")
    public Result<String> createPurchaseRequest(@RequestBody CreatePurchaseRequest req) {
        Shop shop = shopRepository.findByName(req.getShopName())
                .orElseThrow(() -> new RuntimeException("店铺不存在"));

        User buyer = shop.getUser();

        List<ListedProduct> listedProducts = listedProductRepository.findAllById(req.getProductQuantities().keySet());

        // 删除与 ShippingItem 相关的部分，保留 PurchaseItem
        List<PurchaseItem> items = new ArrayList<>();
        BigDecimal totalWholesale = BigDecimal.ZERO;
        BigDecimal totalSale = BigDecimal.ZERO;

        PurchaseRequest request = PurchaseRequest.builder()
                .buyer(buyer)
                .shop(shop)
                .status(PurchaseRequest.Status.PENDING)
                .createdAt(new Date())
                .build();

        // 生成 PurchaseItem
        for (ListedProduct listed : listedProducts) {
            int qty = req.getProductQuantities().get(listed.getId());
            BigDecimal wholesale = listed.getProduct().getPrice().multiply(BigDecimal.valueOf(qty));
            BigDecimal sale = listed.getSalePrice().multiply(BigDecimal.valueOf(qty));

            totalWholesale = totalWholesale.add(wholesale);
            totalSale = totalSale.add(sale);

            // 进货条目
            PurchaseItem pi = PurchaseItem.builder()
                    .listedProduct(listed)
                    .quantity(qty)
                    .wholesalePrice(listed.getProduct().getPrice())
                    .salePrice(listed.getSalePrice())
                    .request(request)
                    .build();
            items.add(pi);
        }

        // 计算总批发价、售价、利润
        request.setTotalWholesale(totalWholesale);
        request.setTotalSale(totalSale);
        request.setTotalProfit(totalSale.subtract(totalWholesale));
        request.setItems(items);  // 只保存 PurchaseItem


        purchaseRequestRepository.save(request);

        return Result.success("创建进货请求成功");
    }

    /**
     * 分页查询某商家的上架商品（后台用）
     */
    @GetMapping("/admin/listed-products")
    public Result<List<ListedProduct>> listByShopName(@RequestParam String shopName,
                                                      @RequestParam int page,
                                                      @RequestParam int size) {
        Shop shop = shopRepository.findByName(shopName)
                .orElseThrow(() -> new RuntimeException("店铺不存在"));

        Page<ListedProduct> pageResult = listedProductRepository.findByShopId(shop.getId(), PageRequest.of(page, size));

        Result.Pagination pagination = new Result.Pagination(
                pageResult.getNumber(),
                pageResult.getTotalPages(),
                pageResult.getTotalElements(),
                pageResult.getSize()
        );
        return Result.success(pageResult.getContent(), pagination);
    }


    @PostMapping("/withdrawal-record/add")
    @Operation(summary = "客服手动添加用户提现记录", description = "手动为指定用户添加提现金额、备注和日期（格式为 yyyy-MM-dd）")
    public Result<String> addWithdrawalByAdmin(@RequestBody AdminWithdrawalRequest request) {
        Optional<User> optionalUser = userRepository.findById(request.getUserId());

        if (optionalUser.isEmpty()) {
            return Result.notFound("用户不存在");
        }

        Date parsedDate;
        try {
            parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse(request.getWithdrawalDate());
        } catch (ParseException e) {
            return Result.badRequest("日期格式错误，应为 yyyy-MM-dd");
        }

        WithdrawalRecord record = WithdrawalRecord.builder()
                .user(optionalUser.get())
                .amount(request.getAmount())
                .remark(request.getRemark())
                .withdrawalTime(parsedDate)
                .build();

        withdrawalRecordRepository.save(record);

        return Result.success("客服手动提现记录添加成功");
    }

    @PostMapping("/set-user-banned")
    @Operation(summary = "设置用户封禁状态", description = "通过 JSON body 传递 userId 和 banned=true/false 来封禁或解封用户")
    public Result<Void> setUserBanned(@RequestBody BanUserRequest req) {
        try {
            userService.setUserBannedStatus(req.getUserId(), req.getBanned());
            return Result.success();
        } catch (Exception e) {
            return Result.failure("设置封禁状态失败：" + e.getMessage());
        }
    }

    @PostMapping("/setCanWithdraw")
    public Result<String> setCanWithdraw(@RequestBody SetCanWithdrawRequest request) {
        try {
            userService.setCanWithdraw(request.getUserId(), request.isCanWithdraw());
            return Result.success("设置成功");
        } catch (IllegalArgumentException e) {
            return Result.badRequest(e.getMessage());
        } catch (Exception e) {
            return Result.failure("系统异常，设置失败");
        }
    }

    @GetMapping("/sales/search-by-username")
    @Operation(summary = "模糊搜索业务员（按用户名）", description = "根据用户名模糊查询，返回身份为 SALES 的业务员")
    public Result<List<Administrator>> searchSalesByUsername(@RequestParam String keyword) {
        List<Administrator> results = administratorService.searchSalesByUsernameLike(keyword);
        return Result.success(results);
    }

}
