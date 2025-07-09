package org.wgtech.wgmall_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import org.wgtech.wgmall_backend.entity.Product;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 查找所有比用户余额小的商品
     * @param price
     * @return
     */
    List<Product> findByPriceLessThanEqual(BigDecimal price);

    /**
     * 查找指定价格范围内的商品
     * @param min
     * @param max
     * @return
     */
    List<Product> findByPriceBetween(BigDecimal min, BigDecimal max);


}
