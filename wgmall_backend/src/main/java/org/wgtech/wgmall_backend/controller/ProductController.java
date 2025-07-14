package org.wgtech.wgmall_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.wgtech.wgmall_backend.dto.ProductUploadRequest;
import org.wgtech.wgmall_backend.entity.Product;
import org.wgtech.wgmall_backend.service.ProductService;
import org.wgtech.wgmall_backend.utils.Result;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品管理相关接口
 */
@RestController // 表示该类是一个 REST 控制器，所有返回值都以 JSON 形式响应
@RequestMapping("/products") // 所有接口的统一前缀为 /products
@Tag(name = "商品接口", description = "用于商品的增删查操作") // Swagger 文档分类
public class ProductController {

    @Autowired
    private ProductService productService; // 商品服务层，处理业务逻辑

    /**
     * 添加新商品（含多图上传）
     *
     * 接口地址：POST /products/add
     *
     * @param name 商品名称
     * @param price 商品价格
     * @param stock 库存数量
     * @param sales 初始销量
     * @param type 商品类型（如 ELECTRONICS、FOOD）
     * @param uploader 上传者昵称（可选）
     * @param images 商品图片数组（支持多张上传）
     * @return 添加成功返回商品信息，失败返回错误提示
     */
    @PostMapping(value = "/add", consumes = "multipart/form-data")
    @Operation(summary = "添加商品（身份“BOSS”的权限）", description = "上传商品信息及多张图片")
    public Result<Product> addProduct(
            @RequestPart("product") ProductUploadRequest productDto,
            @RequestPart("images") MultipartFile images
    ) {
        try {
            Product product = productService.createProduct(
                    productDto.getName(),
                    productDto.getPrice(),
                    productDto.getStock(),
                    productDto.getSales(),
                    productDto.getType(),
                    productDto.getUploader(),
                    images
            );
            return Result.success(product);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure("添加商品失败: " + e.getMessage());
        }
    }


    /**
     * 获取某用户“可购买”的商品列表（价格 <= 用户余额）
     *
     * 用于派单等场景
     *
     * @param username 用户名
     * @return 用户余额可购买的商品列表
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
     * 根据价格区间筛选商品
     *
     * @param min 最低价格
     * @param max 最高价格
     * @return 返回在指定价格区间内的商品列表
     */
    @GetMapping("/price-range")
    @Operation(summary = "获取指定价格范围商品（身份“SALES，BOSS“的权限）", description = "查询商品价格在[min, max]范围内的商品列表")
    public List<Product> getProductsByPriceRange(
            @Parameter(description = "最低价格", required = true)
            @RequestParam BigDecimal min,

            @Parameter(description = "最高价格", required = true)
            @RequestParam BigDecimal max
    ) {
        return productService.getProductsByPriceRange(min, max);
    }

    /**
     * 删除指定 ID 的商品
     *
     * 同时删除本地的图片资源
     *
     * @param id 商品主键 ID
     * @return 删除成功或失败的信息
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品（身份“BOSS”的权限）", description = "根据商品ID彻底删除商品及其图片资源")
    public Result<String> deleteProduct(
            @Parameter(description = "商品ID", required = true)
            @PathVariable Long id
    ) {
        try {
            productService.deleteProductById(id); // 调用服务层删除
            return Result.success("商品删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure("删除失败: " + e.getMessage());
        }
    }

    @GetMapping("/random")
    @Operation(summary = "随机获取8个商品（所有人）", description = "给用户主页用")
    public ResponseEntity<List<Product>> getRandomProducts() {
        return ResponseEntity.ok(productService.getRandomProducts());
    }

    @Operation(summary = "根据商品类型随机获取8个商品（所有人）", description = "给用户在浏览标签下的商品时")
    @GetMapping("/random/type")
    public ResponseEntity<List<Product>> getRandomProductsByType(@RequestParam("type") Product.ProductType type) {
        return ResponseEntity.ok(productService.getRandomProductsByType(type));
    }

    @GetMapping("/search")
    @Operation(summary = "关键词搜索商品（用户）", description = "根据名称或类型精准/模糊匹配，匹配不到就随机返回")
    public Result<List<Product>> searchProducts(
            @Parameter(description = "搜索关键词", required = true)
            @RequestParam String keyword
    ) {
        List<Product> results = productService.searchProductsByKeyword(keyword);
        return Result.success(results);
    }

}
