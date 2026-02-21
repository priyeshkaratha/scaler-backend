package com.scaler.productcatalog.services;

import com.scaler.productcatalog.models.Product;
import org.springframework.data.domain.Page;

public interface SearchService {
    Page<Product> searchProducts(String query, int pageNumber, int pageSize, String sortBy);
}
