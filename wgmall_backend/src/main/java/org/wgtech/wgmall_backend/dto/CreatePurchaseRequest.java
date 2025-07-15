package org.wgtech.wgmall_backend.dto;

import lombok.Data;

import java.util.Map;

@Data
public class CreatePurchaseRequest {
    private String shopName;
    private Map<Long, Integer> productQuantities; // productId → quantity
}
