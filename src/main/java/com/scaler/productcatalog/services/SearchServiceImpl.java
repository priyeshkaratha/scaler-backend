package com.scaler.productcatalog.services;

import com.scaler.productcatalog.models.Product;
import com.scaler.productcatalog.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private ProductRepository productRepository;
    @Override
    public Page<Product> searchProducts(String query, int pageNumber, int pageSize, String sortBy) {
        return productRepository.findByTitle(query, Pageable.ofSize(pageSize).withPage(pageNumber));
    }
}
