package org.wgtech.wgmall_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    Page<Product> findByPriceBetween(BigDecimal min, BigDecimal max, Pageable pageable);

    List<Product> findByType(Product.ProductType type);

    // 随机查找某一类商品的前8个
    @Query(value = "SELECT * FROM products WHERE type = :type ORDER BY RAND() LIMIT 8", nativeQuery = true)
    List<Product> findRandomByType(@Param("type") String type);

    // 随机查找所有商品的前8个
    @Query(value = "SELECT * FROM products ORDER BY RAND() LIMIT 8", nativeQuery = true)
    List<Product> findRandomAll();


    // 4. 随机返回
    @Query(value = "SELECT * FROM products ORDER BY RAND() LIMIT ?1", nativeQuery = true)
    List<Product> findRandomProducts(int limit);

    Page<Product> findByTypeIn(List<Product.ProductType> types, Pageable pageable);

    Page<Product> findByNameIgnoreCase(String name, Pageable pageable);
    Page<Product> findByType(Product.ProductType type, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

}
