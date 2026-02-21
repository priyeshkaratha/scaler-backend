package com.scaler.productcatalog.controllers;

import com.scaler.productcatalog.dtos.CreateCategoryDto;
import com.scaler.productcatalog.models.Category;
import com.scaler.productcatalog.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Page<Category>> getAllCategory(
            @RequestParam("pageNumber") Integer pageNumber,
            @RequestParam("pageSize") Integer pageSize,
            @RequestParam("fieldName") String fieldName){
        return new ResponseEntity<>(categoryService.getAllCategories(pageNumber, pageSize, fieldName),
                HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody CreateCategoryDto category){
        return new ResponseEntity<>(categoryService.createCategory(category.getTitle()), HttpStatus.OK);
    }
}
