package com.scaler.productcatalog.services;

import com.scaler.productcatalog.models.Category;
import com.scaler.productcatalog.models.Product;

import java.util.List;
import java.util.NoSuchElementException;

public interface CategoryService {
    Category createCategory(String categoryTitle);
    List<Product> getProductsInCategory(String category) throws NoSuchElementException;
    List<Category> getAllCategories() throws NullPointerException;
    Category getCategoryByTitle(String category) throws NoSuchElementException;
}
