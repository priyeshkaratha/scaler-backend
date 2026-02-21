package com.scaler.productcatalog.services;

import com.scaler.productcatalog.exceptions.ProductNotFoundException;
import com.scaler.productcatalog.models.Category;
import com.scaler.productcatalog.models.Product;
import org.springframework.data.domain.Page;

public interface ProductService {
    Product getProductById(Long id) throws ProductNotFoundException;
    Page<Product> getAllProducts(int pageNumber, int pageSize, String fieldName) throws ProductNotFoundException;
    Product createProduct(Product product, String category);
    Product deleteProduct(Long id) throws ProductNotFoundException;
    Product updateProduct(Long id, Product product, String category) throws ProductNotFoundException;
}
