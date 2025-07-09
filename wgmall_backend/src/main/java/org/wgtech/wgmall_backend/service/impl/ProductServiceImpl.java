package org.wgtech.wgmall_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.wgtech.wgmall_backend.entity.Product;
import org.wgtech.wgmall_backend.entity.ProductImage;
import org.wgtech.wgmall_backend.repository.ProductRepository;
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

        String uploadDir = System.getProperty("user.dir") + "/uploads/products/";
        Files.createDirectories(Paths.get(uploadDir));

        List<ProductImage> imageList = new ArrayList<>();

        for (int i = 0; i < images.length; i++) {
            MultipartFile file = images[i];
            String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'));

            // 用 product.getId() 来构建文件名
            String fileName = product.getId() + "_img" + (i + 1) + ext;
            String fullPath = uploadDir + fileName;

            File targetFile = new File(fullPath);
            targetFile.getParentFile().mkdirs(); // 确保目录存在
            file.transferTo(targetFile); // 保存图片

            ProductImage image = ProductImage.builder()
                    .imagePath(fullPath.replace("\\", "/"))
                    .sortOrder(i + 1)
                    .product(product)
                    .build();

            imageList.add(image);
        }

        product.setImages(imageList);

        // 再保存一次，让图片也存进去（级联）
        return productRepository.save(product);
    }

}
