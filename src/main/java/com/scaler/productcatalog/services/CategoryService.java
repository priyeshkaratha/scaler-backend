package com.scaler.productcatalog.services;

import com.scaler.productcatalog.models.Category;
import com.scaler.productcatalog.models.Product;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface CategoryService {
    Category createCategory(String categoryTitle);
    Optional<Category> getCategoryByTitle(String category) throws NoSuchElementException;
    Page<Category> getAllCategories(int pageNumber, int pageSize, String fieldName);
}
