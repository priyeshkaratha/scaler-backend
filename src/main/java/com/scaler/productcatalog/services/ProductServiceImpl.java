package com.scaler.productcatalog.services;

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
    public Product getProductById(Long id) throws ProductNotFoundException {
       return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }

    @Override
    public Page<Product> getAllProducts(int pageNumber, int pageSize, String fieldName) throws ProductNotFoundException {
        Page<Product> listOfProducts = productRepository.findAll(
                PageRequest.of(
                        pageNumber,
                        pageSize,
                        Sort.by(fieldName).ascending()
                )
        );
        if(!listOfProducts.hasContent()){
            throw new ProductNotFoundException("No products found");
        }
        return listOfProducts;
    }

    @Override
    public Product createProduct(Product product, String category) {
        try {
            Category categoryObject = categoryService.getCategoryByTitle(category);
            product.setCategory(categoryObject);
        } catch (Exception e) {
            Category categoryObject = categoryService.createCategory(category);
            product.setCategory(categoryObject);
        }
        return productRepository.save(product);
    }

    @Override
    public Product deleteProduct(Long id) throws ProductNotFoundException {
        return null;
    }

    @Override
    public Product updateProduct(Long id, String title, String description, Double price, Category category, String imageUrl) throws ProductNotFoundException {
        return null;
    }
}
