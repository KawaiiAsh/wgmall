package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.wgtech.wgmall_backend.entity.Product;
import org.wgtech.wgmall_backend.service.ProductService;
import org.wgtech.wgmall_backend.utils.Result;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品管理相关接口
 */
@RestController
@RequestMapping("/products")
@Tag(name = "商品接口", description = "用于商品的增删查操作")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 添加新商品
     */
    @PostMapping("/add")
    @Operation(summary = "添加商品", description = "上传商品信息及多张图片，图片自动保存在本地")
    public Result<Product> addProduct(
            @Parameter(description = "商品名称", required = true)
            @RequestParam String name,

            @Parameter(description = "商品价格", required = true)
            @RequestParam BigDecimal price,

            @Parameter(description = "商品描述", required = false)
            @RequestParam(required = false) String description,

            @Parameter(description = "库存数量", required = true)
            @RequestParam Integer stock,

            @Parameter(description = "初始销量", required = true)
            @RequestParam Integer sales,

            @Parameter(description = "商品类型（枚举值如：ELECTRONICS、FOOD）", required = true)
            @RequestParam String type,

            @Parameter(description = "上传者昵称（默认为“业务员”）", required = false)
            @RequestParam(required = false) String uploader,

            @Parameter(description = "商品图片数组（最多支持多张）", required = true)
            @RequestParam("images") MultipartFile[] images
    ) {
        try {
            Product product = productService.createProduct(
                    name, price, description, stock, sales, type, uploader, images
            );
            return Result.success(product);
        } catch (Exception e) {
            e.printStackTrace(); // 日志
            return Result.failure("添加商品失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定用户可购买的商品
     */
    @GetMapping("/affordable")
    @Operation(summary = "获取所有价格比用户余额小的商品", description = "用来随机派单")
    public List<Product> getAffordableProducts(
            @Parameter(description = "用户名", required = true)
            @RequestParam String username
    ) {
        return productService.getProductsAffordableByUser(username);
    }

    /**
     * 获取指定价格范围内的商品
     */
    @GetMapping("/price-range")
    @Operation(summary = "获取指定价格范围商品", description = "查询商品价格在[min, max]范围内的商品列表")
    public List<Product> getProductsByPriceRange(
            @Parameter(description = "最低价格", required = true)
            @RequestParam BigDecimal min,
            @Parameter(description = "最高价格", required = true)
            @RequestParam BigDecimal max
    ) {
        return productService.getProductsByPriceRange(min, max);
    }

    /**
     * 根据ID删除商品
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品", description = "根据商品ID彻底删除商品及其图片资源")
    public Result<String> deleteProduct(
            @Parameter(description = "商品ID", required = true)
            @PathVariable Long id
    ) {
        try {
            productService.deleteProductById(id);
            return Result.success("商品删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure("删除失败: " + e.getMessage());
        }
    }
}
