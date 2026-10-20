package com.scaler.productcatalog.controllers;

import com.scaler.productcatalog.dtos.ProductCreateRequestDto;
import com.scaler.productcatalog.dtos.ProductResponseDto;
import com.scaler.productcatalog.services.ProductService;
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
@RequestMapping("/products")
public class ProductController {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "title", "price", "createdAt", "updatedAt");

    @Autowired
    private ProductService productService;

    @GetMapping()
    public ResponseEntity<Page<ProductResponseDto>> getAllProducts(
            @RequestParam(defaultValue = "0") @Min(0) int pageNumber,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int pageSize,
            @RequestParam(defaultValue = "id") String fieldName) {
        String sortField = ALLOWED_SORT_FIELDS.contains(fieldName) ? fieldName : "id";
        return ResponseEntity.ok(productService.getAllProducts(pageNumber, pageSize, sortField).map(ProductResponseDto::from));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(ProductResponseDto.from(productService.getProductById(id)));
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductCreateRequestDto productDto) {
        return new ResponseEntity<>(ProductResponseDto.from(
                productService.createProduct(productDto.getProduct(), productDto.getCategory())), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductResponseDto> deleteProduct(@PathVariable Long id) {
        return ResponseEntity.ok(ProductResponseDto.from(productService.deleteProduct(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductCreateRequestDto productDto) {
        return ResponseEntity.ok(ProductResponseDto.from(
                productService.updateProduct(id, productDto.getProduct(), productDto.getCategory())));
    }
}
