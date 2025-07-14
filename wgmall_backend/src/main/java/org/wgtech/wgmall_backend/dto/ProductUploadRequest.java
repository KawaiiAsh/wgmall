package org.wgtech.wgmall_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUploadRequest {
    private String name;
    private BigDecimal price;
    private Integer stock;
    private Integer sales;
    private String type;
    private String uploader;

    // Getter/Setter 或使用 Lombok 的 @Data
}
