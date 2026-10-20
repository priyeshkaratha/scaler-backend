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

@Service
public class ProductServiceImpl implements ProductService{
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;

    @Override
    public Product getProductById(Long id) {
       return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    }

    @Override
    public Page<Product> getAllProducts(int pageNumber, int pageSize, String fieldName) {
        return productRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by(fieldName).ascending()));
    }

    @Override
    public Product createProduct(Product product, String category) {
        product.setCategory(findCategory(category));
        product.setCreatedAt(new Date());
        product.setUpdatedAt(new Date());
        product.setIsActive(true);
        return productRepository.save(product);
    }

    @Override
    public Product deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
        return product;
    }

    @Override
    public Product updateProduct(Long productId, Product product, String category) {
        Product productObject = getProductById(productId);
        productObject.setTitle(product.getTitle());
        productObject.setDescription(product.getDescription());
        productObject.setPrice(product.getPrice());
        productObject.setImageUrl(product.getImageUrl());
        productObject.setCategory(findCategory(category));
        productObject.setUpdatedAt(new Date());
        productObject.setIsActive(true);
        return productRepository.save(productObject);
    }

    private Category findCategory(String title) {
        return categoryService.getCategoryByTitle(title)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with title: " + title));
    }
}
