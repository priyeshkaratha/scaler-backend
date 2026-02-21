package com.scaler.productcatalog.services;

import com.scaler.productcatalog.models.Category;
import com.scaler.productcatalog.models.Product;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface CategoryService {
    Category createCategory(String categoryTitle);
    Optional<Category> getCategoryByTitle(String category) throws NoSuchElementException;
}
