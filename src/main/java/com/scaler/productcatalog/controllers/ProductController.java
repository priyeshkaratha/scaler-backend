package com.scaler.productcatalog.controllers;

import com.scaler.productcatalog.dtos.ProductCreateRequestDto;
import com.scaler.productcatalog.exceptions.ProductNotFoundException;
import com.scaler.productcatalog.models.Product;
import com.scaler.productcatalog.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping()
    public Page<Product> getAllProducts(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String fieldName) {
        try {
            return productService.getAllProducts(pageNumber, pageSize, fieldName);
        } catch (ProductNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) throws ProductNotFoundException {
        return productService.getProductById(id);
    }

    @PostMapping
    public Product createProduct(@RequestBody ProductCreateRequestDto productDto) {
        Product product = productService.createProduct(productDto.getProduct(), productDto.getCategory());
        return product;
    }

    @DeleteMapping("/{id}")
    public Product deleteProduct(@PathVariable Long id) throws ProductNotFoundException {
        return productService.deleteProduct(id);
    }
}
