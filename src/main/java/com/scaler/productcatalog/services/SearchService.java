package com.scaler.productcatalog.services;

import com.scaler.productcatalog.dtos.SearchRequestDto;
import com.scaler.productcatalog.models.Product;
import org.springframework.data.domain.Page;

public interface SearchService {
    Page<Product> searchProducts(SearchRequestDto searchRequestDto);
}
