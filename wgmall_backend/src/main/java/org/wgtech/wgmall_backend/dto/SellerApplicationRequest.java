package org.wgtech.wgmall_backend.dto;

import lombok.Data;
import org.wgtech.wgmall_backend.entity.Product;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SellerApplicationRequest {
    private String username; // ✅ 新增字段
    private String shopName;
    private String shopDescription;
    private String businessPhone;
    private String profession;
    private BigDecimal monthlyIncome;
    private List<Product.ProductType> mainProductTypes;
    private String idFrontImage;
    private String idBackImage;
}
