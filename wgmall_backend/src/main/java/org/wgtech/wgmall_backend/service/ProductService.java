package org.wgtech.wgmall_backend.service;

import org.springframework.web.multipart.MultipartFile;
import org.wgtech.wgmall_backend.entity.Product;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    Product createProduct(String name, BigDecimal price, String description, Integer stock,
                          Integer sales, String type, String uploader, MultipartFile[] images) throws IOException;

}
