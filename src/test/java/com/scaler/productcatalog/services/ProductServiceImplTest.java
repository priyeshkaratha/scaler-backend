package com.scaler.productcatalog.services;

import com.scaler.productcatalog.exceptions.CategoryNotFoundException;
import com.scaler.productcatalog.exceptions.ProductNotFoundException;
import com.scaler.productcatalog.models.Category;
import com.scaler.productcatalog.models.Product;
import com.scaler.productcatalog.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryService categoryService;
    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void createProductLinksExistingCategoryAndSetsAuditFields() {
        Category phones = new Category();
        phones.setTitle("Smartphones");
        when(categoryService.getCategoryByTitle("Smartphones")).thenReturn(Optional.of(phones));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product saved = productService.createProduct(new Product(), "Smartphones");

        assertThat(saved.getCategory()).isSameAs(phones);
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getIsActive()).isTrue();
    }

    @Test
    void createProductFailsForUnknownCategory() {
        when(categoryService.getCategoryByTitle("Unknown")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> productService.createProduct(new Product(), "Unknown"))
                .isInstanceOf(CategoryNotFoundException.class);
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProductFailsForUnknownCategoryInsteadOfCreatingIt() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(new Product()));
        when(categoryService.getCategoryByTitle("Unknown")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> productService.updateProduct(1L, new Product(), "Unknown"))
                .isInstanceOf(CategoryNotFoundException.class);
        verify(categoryService, never()).createCategory(any());
    }

    @Test
    void getProductByIdThrowsWhenMissing() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> productService.getProductById(99L)).isInstanceOf(ProductNotFoundException.class);
    }
}
