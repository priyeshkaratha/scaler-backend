package com.scaler.productcatalog.controllers;

import com.scaler.productcatalog.dtos.ProductCreateRequestDto;
import com.scaler.productcatalog.exceptions.ProductNotFoundException;
import com.scaler.productcatalog.models.Product;
import com.scaler.productcatalog.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/products")
public class ProductController {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "title", "price", "createdAt", "updatedAt");

    @Autowired
    private ProductService productService;

    @GetMapping()
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String fieldName) throws ProductNotFoundException {
        String sortField = ALLOWED_SORT_FIELDS.contains(fieldName) ? fieldName : "id";
        Page<Product> products =  productService.getAllProducts(pageNumber, pageSize, sortField);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) throws ProductNotFoundException {
        return new ResponseEntity<>(productService.getProductById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductCreateRequestDto productDto) {
        Product product = productService.createProduct(
                productDto.getProduct(), productDto.getCategory());
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable Long id) throws ProductNotFoundException {
        return new ResponseEntity<>(productService.deleteProduct(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody ProductCreateRequestDto productDto) throws ProductNotFoundException {
        Product product = productService.updateProduct(id, productDto.getProduct(), productDto.getCategory());
        return new ResponseEntity<>(product, HttpStatus.OK);
    }
}
