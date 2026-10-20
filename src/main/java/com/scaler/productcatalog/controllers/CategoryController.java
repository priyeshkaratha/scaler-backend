package com.scaler.productcatalog.controllers;

import com.scaler.productcatalog.dtos.CategoryResponseDto;
import com.scaler.productcatalog.dtos.CreateCategoryDto;
import com.scaler.productcatalog.services.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Validated
@RestController
@RequestMapping("/category")
public class CategoryController {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "title", "createdAt", "updatedAt");

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Page<CategoryResponseDto>> getAllCategory(
            @RequestParam(defaultValue = "0") @Min(0) int pageNumber,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int pageSize,
            @RequestParam(defaultValue = "id") String fieldName){
        String sortField = ALLOWED_SORT_FIELDS.contains(fieldName) ? fieldName : "id";
        return ResponseEntity.ok(categoryService.getAllCategories(pageNumber, pageSize, sortField).map(CategoryResponseDto::from));
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CreateCategoryDto category){
        return new ResponseEntity<>(CategoryResponseDto.from(categoryService.createCategory(category.getTitle())), HttpStatus.CREATED);
    }
}
