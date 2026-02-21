package com.scaler.productcatalog.services;

import com.scaler.productcatalog.exceptions.CategoryNotFoundException;
import com.scaler.productcatalog.exceptions.ProductNotFoundException;
import com.scaler.productcatalog.models.Category;
import com.scaler.productcatalog.models.Product;
import com.scaler.productcatalog.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService{
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;

    @Override
    public Product getProductById(Long id) throws ProductNotFoundException {
       return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }

    @Override
    public Page<Product> getAllProducts(int pageNumber, int pageSize, String fieldName) throws ProductNotFoundException {
        return productRepository.findAll(
                PageRequest.of(
                        pageNumber,
                        pageSize,
                        Sort.by(fieldName).ascending()
                )
        );
    }

    @Override
    public Product createProduct(Product product, String category) {
        Optional<Category> optionalCategory = categoryService.getCategoryByTitle(category);
        if (optionalCategory.isEmpty()) {
            throw new CategoryNotFoundException("Category not found with title: " + category + "");
        }
        product.setCategory(optionalCategory.get());
        product.setCreatedAt(new Date());
        product.setUpdatedAt(new Date());
        product.setIsActive(true);
        return productRepository.save(product);
    }

    @Override
    public Product deleteProduct(Long id) throws ProductNotFoundException {
        try {
            Product product = getProductById(id);
            productRepository.deleteById(id);
            return product;
        } catch (ProductNotFoundException e) {
            throw new ProductNotFoundException("Product not found");
        }
    }

    @Override
    public Product updateProduct(Long productId, Product product, String category) throws ProductNotFoundException {
        Product productObject = getProductById(productId);
        productObject.setTitle(product.getTitle());
        productObject.setDescription(product.getDescription());
        productObject.setPrice(product.getPrice());
        productObject.setImageUrl(product.getImageUrl());
        Optional<Category> optionalCategory = categoryService.getCategoryByTitle(category);
        if (optionalCategory.isPresent()) {
            Category categoryObject = optionalCategory.get();
            productObject.setCategory(categoryObject);
        } else {
            Category categoryObject = categoryService.createCategory(category);
            productObject.setCategory(categoryObject);
        }
        productObject.setUpdatedAt(new Date());
        productObject.setIsActive(true);
        return productRepository.save(productObject);
    }
}
