package org.wgtech.wgmall_backend.service;

import org.springframework.web.multipart.MultipartFile;
import org.wgtech.wgmall_backend.entity.Product;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    /**
     * 创建商品
     * @param name
     * @param price
     * @param description
     * @param stock
     * @param sales
     * @param type
     * @param uploader
     * @param images
     * @return
     * @throws IOException
     */
    Product createProduct(String name, BigDecimal price, String description, Integer stock,
                          Integer sales, String type, String uploader, MultipartFile[] images) throws IOException;


    /**
     * 查询所有比用户余额少的商品
     * @param username
     * @return
     */
    List<Product> getProductsAffordableByUser(String username);

    /**
     * 查找指定价格范围内的商品
     * @param min
     * @param max
     * @return
     */
    List<Product> getProductsByPriceRange(BigDecimal min, BigDecimal max);

    /**
     * 删除一个指定商品
     * @param productId
     * @throws IOException
     */
    void deleteProductById(Long productId) throws IOException;



}
