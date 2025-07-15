package org.wgtech.wgmall_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.wgtech.wgmall_backend.entity.ListedProduct;

public interface ListedProductRepository extends JpaRepository<ListedProduct, Long> {

    long countByShopId(Long shopId);

    Page<ListedProduct> findByShopId(Long shopId, Pageable pageable);
}
