package com.scaler.productcatalog.services;

import com.scaler.productcatalog.exceptions.CategoryAlreadyExistsException;
import com.scaler.productcatalog.models.Category;
import com.scaler.productcatalog.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category createCategory(String categoryTitle) {
        if (categoryRepository.findByTitle(categoryTitle).isPresent()) {
            throw new CategoryAlreadyExistsException("Category already exists with title: " + categoryTitle);
        }
        Category category = new Category();
        category.setTitle(categoryTitle);
        category.setIsActive(true);
        category.setCreatedAt(new Date());
        category.setUpdatedAt(new Date());
        return categoryRepository.save(category);
    }

    @Override
    public Optional<Category> getCategoryByTitle(String category) {
        return categoryRepository.findByTitle(category);
    }

    @Override
    public Page<Category> getAllCategories(int pageNumber, int pageSize, String fieldName) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(fieldName).ascending());
        return categoryRepository.findAll(pageable);
    }
}
