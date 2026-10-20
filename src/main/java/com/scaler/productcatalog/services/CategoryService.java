package com.scaler.productcatalog.services;

import com.scaler.productcatalog.models.Category;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface CategoryService {
    Category createCategory(String categoryTitle);
    Optional<Category> getCategoryByTitle(String category);
    Page<Category> getAllCategories(int pageNumber, int pageSize, String fieldName);
}
