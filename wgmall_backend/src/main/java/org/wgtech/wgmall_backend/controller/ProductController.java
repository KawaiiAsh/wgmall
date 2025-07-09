package org.wgtech.wgmall_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.wgtech.wgmall_backend.entity.Product;
import org.wgtech.wgmall_backend.entity.ProductImage;
import org.wgtech.wgmall_backend.service.ProductService;
import org.wgtech.wgmall_backend.utils.Result;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public Result<Product> addProduct(
            @RequestParam String name,
            @RequestParam BigDecimal price,
            @RequestParam(required = false) String description,
            @RequestParam Integer stock,
            @RequestParam Integer sales,
            @RequestParam String type,
            @RequestParam(required = false) String uploader,
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


}
