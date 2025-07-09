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

    @Override
    public Product createProduct(String name, BigDecimal price, String description, Integer stock,
                                 Integer sales, String type, String uploader, MultipartFile[] images) throws IOException {

        Product product = Product.builder()
                .name(name)
                .price(price)
                .description(description)
                .stock(stock)
                .sales(sales)
                .uploader(uploader)
                .type(Product.ProductType.valueOf(type))
                .build();

        // 先保存 product，让数据库生成 ID
        product = productRepository.save(product);

        // 图片保存的实际物理路径（服务器本地目录）
        String uploadDir = System.getProperty("user.dir") + "/uploads/products/";
        Files.createDirectories(Paths.get(uploadDir));

        List<ProductImage> imageList = new ArrayList<>();

        for (int i = 0; i < images.length; i++) {
            MultipartFile file = images[i];
            String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'));

            String fileName = product.getId() + "_img" + (i + 1) + ext;
            String fullPath = uploadDir + fileName;

            // 保存文件到本地
            File targetFile = new File(fullPath);
            file.transferTo(targetFile);

            // ⚠️ 保存相对路径而不是绝对路径
            String relativePath = "/uploads/products/" + fileName;

            ProductImage image = ProductImage.builder()
                    .imagePath(relativePath)
                    .sortOrder(i + 1)
                    .product(product)
                    .build();

            imageList.add(image);
        }

        product.setImages(imageList);

        return productRepository.save(product);
    }


    @Override
    public List<Product> getProductsAffordableByUser(String username) {
        // 1. 查询用户
        User user = userRepository.findByUsername(username).get();

        // 2. 获取余额
        BigDecimal balance = BigDecimal.valueOf(user.getBalance());

        // 3. 查询所有商品价格 ≤ 余额
        List<Product> products = productRepository.findByPriceLessThanEqual(balance);

        // 4. 为每个商品设置第一张图路径（如 /uploads/products/1_img1.jpg）
        for (Product product : products) {
            String firstImagePath = "/uploads/products/" + product.getId() + "_img1.jpg";
            product.setFirstImagePath("/uploads/products/" + product.getId() + "_img1.jpg");
        }

        return products;
    }


    @Override
    public List<Product> getProductsByPriceRange(BigDecimal min, BigDecimal max) {
        List<Product> products = productRepository.findByPriceBetween(min, max);

        // 为每个商品设置第一张图片路径
        for (Product product : products) {
            product.setFirstImagePath("/uploads/products/" + product.getId() + "_img1.jpg");
        }

        return products;
    }

    @Override
    public void deleteProductById(Long productId) throws IOException {
        // 1. 查询商品
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在，ID: " + productId));

        // 2. 删除每张图片文件
        for (ProductImage image : product.getImages()) {
            String relativePath = image.getImagePath();  // 例: /uploads/products/6_img1.jpg

            // 拼接服务器上的完整路径
            String absolutePath = System.getProperty("user.dir") + relativePath;

            File file = new File(absolutePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    System.err.println("⚠️ 图片文件删除失败：" + absolutePath);
                }
            } else {
                System.out.println("🟡 图片不存在，跳过删除：" + absolutePath);
            }
        }

        // 3. 删除商品记录（级联删除 ProductImage 数据）
        productRepository.delete(product);
    }

}
