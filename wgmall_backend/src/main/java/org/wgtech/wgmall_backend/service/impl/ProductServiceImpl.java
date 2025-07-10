package org.wgtech.wgmall_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.wgtech.wgmall_backend.entity.Product;
import org.wgtech.wgmall_backend.entity.ProductImage;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.repository.ProductRepository;
import org.wgtech.wgmall_backend.repository.UserRepository;
import org.wgtech.wgmall_backend.service.ProductService;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 创建商品（包含上传图片）
     */
    @Override
    public Product createProduct(String name, BigDecimal price, Integer stock,
                                 Integer sales, String type, String uploader, MultipartFile[] images) throws IOException {

        // 构造商品对象（此时没有图片）
        Product product = Product.builder()
                .name(name)
                .price(price)
                .stock(stock)
                .sales(sales)
                .uploader(uploader)
                .type(Product.ProductType.valueOf(type)) // 枚举类型转换
                .build();

        // 保存商品，生成数据库主键 ID
        product = productRepository.save(product);

        // 构建本地上传目录路径（在服务器本地创建目录）
        String uploadDir = System.getProperty("user.dir") + "/uploads/products/";
        Files.createDirectories(Paths.get(uploadDir));

        // 用于保存所有图片对象
        List<ProductImage> imageList = new ArrayList<>();

        // 遍历每张图片并保存到本地
        for (int i = 0; i < images.length; i++) {
            MultipartFile file = images[i];
            String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.')); // 获取后缀名

            // 构建文件名：如 12_img1.jpg
            String fileName = product.getId() + "_img" + (i + 1) + ext;
            String fullPath = uploadDir + fileName;

            // 保存文件到本地磁盘
            File targetFile = new File(fullPath);
            file.transferTo(targetFile);

            // 保存相对路径（用于前端访问）
            String relativePath = "/uploads/products/" + fileName;

            // 构造图片实体并加入列表
            ProductImage image = ProductImage.builder()
                    .imagePath(relativePath)
                    .sortOrder(i + 1)
                    .product(product)
                    .build();

            imageList.add(image);
        }

        // 设置商品的图片列表
        product.setImages(imageList);

        // 再次保存商品，包含图片信息
        return productRepository.save(product);
    }

    /**
     * 获取用户余额范围内可购买的商品
     */
    @Override
    public List<Product> getProductsAffordableByUser(String username) {
        // 1. 根据用户名查询用户
        User user = userRepository.findByUsername(username).get();

        // 2. 获取用户余额
        BigDecimal balance = BigDecimal.valueOf(user.getBalance());

        // 3. 查询价格 <= 余额的商品
        List<Product> products = productRepository.findByPriceLessThanEqual(balance);

        // 4. 给每个商品设置默认展示图路径（第1张）
        for (Product product : products) {
            product.setFirstImagePath("/uploads/products/" + product.getId() + "_img1.jpg");
        }

        return products;
    }

    /**
     * 根据价格区间获取商品列表
     */
    @Override
    public List<Product> getProductsByPriceRange(BigDecimal min, BigDecimal max) {
        // 查询价格在指定范围内的商品
        List<Product> products = productRepository.findByPriceBetween(min, max);

        // 设置每个商品的第一张图片路径
        for (Product product : products) {
            product.setFirstImagePath("/uploads/products/" + product.getId() + "_img1.jpg");
        }

        return products;
    }

    /**
     * 根据商品 ID 删除商品（同时删除本地图片）
     */
    @Override
    public void deleteProductById(Long productId) throws IOException {
        // 1. 查询商品对象，不存在则抛异常
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在，ID: " + productId));

        // 2. 删除本地保存的每张图片文件
        for (ProductImage image : product.getImages()) {
            String relativePath = image.getImagePath();  // 示例：/uploads/products/6_img1.jpg

            // 拼接绝对路径
            String absolutePath = System.getProperty("user.dir") + relativePath;

            File file = new File(absolutePath);
            if (file.exists()) {
                boolean deleted = file.delete(); // 删除文件
                if (!deleted) {
                    System.err.println("⚠️ 图片文件删除失败：" + absolutePath);
                }
            } else {
                System.out.println("🟡 图片不存在，跳过删除：" + absolutePath);
            }
        }

        // 3. 删除商品记录（ProductImage 将被级联删除）
        productRepository.delete(product);
    }

}
