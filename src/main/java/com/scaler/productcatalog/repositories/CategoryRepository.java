package com.scaler.productcatalog.repositories;

import com.scaler.productcatalog.models.Category;
import com.scaler.productcatalog.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByTitle(String title);
    Page<Category> findAll(Pageable pageable);
}
