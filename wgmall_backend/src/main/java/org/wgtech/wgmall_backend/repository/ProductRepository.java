package org.wgtech.wgmall_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.wgtech.wgmall_backend.entity.Product;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 查询价格小于等于指定值的商品
     * @param price 用户当前余额
     * @return 可负担的商品列表
     */
    List<Product> findByPriceLessThanEqual(BigDecimal price);

    /**
     * 查询指定价格区间的商品
     * @param min 最低价格
     * @param max 最高价格
     * @return 商品列表
     */
    List<Product> findByPriceBetween(BigDecimal min, BigDecimal max);
}
