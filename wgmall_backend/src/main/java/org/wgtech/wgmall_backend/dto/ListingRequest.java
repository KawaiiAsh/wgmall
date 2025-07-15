package org.wgtech.wgmall_backend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class ListingRequest {

    // 卖家用户名
    private String username;

    // 要上架的商品ID列表
    private List<Long> productIds;

    // 每个商品的上架价格映射：key 是 productId，value 是上架售价
    private Map<Long, BigDecimal> salePrices;
}
