package org.wgtech.wgmall_backend.service;

import org.springframework.web.multipart.MultipartFile;
import org.wgtech.wgmall_backend.entity.Product;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    /**
     * 创建商品（支持上传图片）
     * @param name 商品名
     * @param price 商品价格
     * @param stock 库存
     * @param sales 销量
     * @param type 商品类型（枚举）
     * @param uploader 上传人用户名
     * @param image 图片文件
     * @return 创建成功的商品对象
     * @throws IOException 上传图片失败
     */
    Product createProduct(String name, BigDecimal price, Integer stock,
                          Integer sales, String type, String uploader, MultipartFile image) throws IOException;

    /**
     * 获取当前用户余额范围内可购买的商品
     * @param username 用户名
     * @return 商品列表
     */
    List<Product> getProductsAffordableByUser(String username);

    /**
     * 根据价格区间筛选商品
     * @param min 最低价格
     * @param max 最高价格
     * @return 商品列表
     */
    List<Product> getProductsByPriceRange(BigDecimal min, BigDecimal max);

    /**
     * 根据商品ID删除商品（同时删除图片）
     * @param productId 商品ID
     * @throws IOException 文件删除失败
     */
    void deleteProductById(Long productId) throws IOException;

    List<Product> getRandomProducts();
    List<Product> getRandomProductsByType(Product.ProductType type);

}
