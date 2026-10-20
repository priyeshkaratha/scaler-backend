package com.scaler.productcatalog.services;

import com.scaler.productcatalog.models.Product;
import org.springframework.data.domain.Page;

public interface ProductService {
    Product getProductById(Long id);
    Page<Product> getAllProducts(int pageNumber, int pageSize, String fieldName);
    Product createProduct(Product product, String category);
    Product deleteProduct(Long id);
    Product updateProduct(Long id, Product product, String category);
}
