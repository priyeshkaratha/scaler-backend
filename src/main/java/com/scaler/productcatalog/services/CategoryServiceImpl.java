package com.scaler.productcatalog.services;

import com.scaler.productcatalog.models.Category;
import com.scaler.productcatalog.models.Product;
import com.scaler.productcatalog.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{
    @Autowired
    private CategoryRepository categoryRepository;
    @Override
    public Category createCategory(String categoryTitle) {
        Category category = new Category();
        category.setTitle(categoryTitle);
        category.setIsActive(true);
        category.setCreatedAt(new java.util.Date());
        category.setUpdatedAt(new java.util.Date());
        category = categoryRepository.save(category);
        return category;
    }

    @Override
    public Optional<Category> getCategoryByTitle(String category) throws NoSuchElementException {
        return categoryRepository.findByTitle(category);
    }
}
