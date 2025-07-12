package org.wgtech.wgmall_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.wgtech.wgmall_backend.entity.Product;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.repository.ProductRepository;
import org.wgtech.wgmall_backend.repository.UserRepository;
import org.wgtech.wgmall_backend.service.ProductService;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 创建商品（包含上传单张图片）
     */
    @Override
    public Product createProduct(String name, BigDecimal price, Integer stock,
                                 Integer sales, String type, String uploader, MultipartFile image) throws IOException {

        // 检查图片是否为空
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("图片不能为空");
        }

        // 构建本地上传目录路径
        String uploadDir = System.getProperty("user.dir") + "/uploads/products/";
        Files.createDirectories(Paths.get(uploadDir));

        // 构建商品对象（初始未保存图片路径）
        Product product = Product.builder()
                .name(name)
                .price(price)
                .stock(stock)
                .sales(sales)
                .uploader(uploader)
                .type(Product.ProductType.valueOf(type))
                .build();

        // 先保存商品，获得数据库主键 ID
        product = productRepository.save(product);

        // 构建图片文件名，如 12_main.jpg
        String ext = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf('.'));
        String fileName = product.getId() + "_main" + ext;
        String fullPath = uploadDir + fileName;

        // 保存图片到本地
        File targetFile = new File(fullPath);
        image.transferTo(targetFile);

        // 设置图片路径（供前端访问）
        String relativePath = "/uploads/products/" + fileName;
        product.setImagePath(relativePath);

        // 再次保存商品，写入图片路径
        return productRepository.save(product);
    }

    /**
     * 获取用户余额范围内可购买的商品
     */
    @Override
    public List<Product> getProductsAffordableByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在：" + username));

        BigDecimal balance = user.getBalance(); // ✅ 直接用
        return productRepository.findByPriceLessThanEqual(balance);
    }

    /**
     * 根据价格区间获取商品列表
     */
    @Override
    public List<Product> getProductsByPriceRange(BigDecimal min, BigDecimal max) {
        return productRepository.findByPriceBetween(min, max);
    }

    /**
     * 根据商品 ID 删除商品（同时删除本地图片）
     */
    @Override
    public void deleteProductById(Long productId) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在，ID: " + productId));

        // 删除图片文件
        if (product.getImagePath() != null) {
            String absolutePath = System.getProperty("user.dir") + product.getImagePath();
            File file = new File(absolutePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    System.err.println("⚠️ 图片文件删除失败：" + absolutePath);
                }
            }
        }

        // 删除商品记录
        productRepository.delete(product);
    }

    /**
     * 随机获取商品（示例实现）
     */
    @Override
    public List<Product> getRandomProducts() {
        List<Product> all = productRepository.findAll();
        Collections.shuffle(all, new Random());
        return all.stream().limit(8).toList();
    }

    /**
     * 随机获取指定类型的商品
     */
    @Override
    public List<Product> getRandomProductsByType(Product.ProductType type) {
        List<Product> all = productRepository.findByType(type);
        Collections.shuffle(all, new Random());
        return all.stream().limit(8).toList();
    }

    public List<Product> searchProductsByKeyword(String keyword) {
        // 精准匹配名称
        List<Product> nameMatches = productRepository.findByNameIgnoreCase(keyword);
        if (!nameMatches.isEmpty()) return nameMatches;

        // 尝试匹配枚举类型
        try {
            Product.ProductType type = Product.ProductType.valueOf(keyword.toUpperCase());
            List<Product> typeMatches = productRepository.findByType(type);
            if (!typeMatches.isEmpty()) return typeMatches;
        } catch (IllegalArgumentException ignored) {}

        // 模糊匹配名称
        List<Product> fuzzyMatches = productRepository.findByNameContainingIgnoreCase(keyword);
        if (!fuzzyMatches.isEmpty()) return fuzzyMatches;

        // 最后随机返回
        return productRepository.findRandomProducts(20);
    }

}
