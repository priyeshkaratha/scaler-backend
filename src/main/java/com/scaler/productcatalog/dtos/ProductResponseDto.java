package com.scaler.productcatalog.dtos;

import com.scaler.productcatalog.models.Product;

import java.util.Date;

public record ProductResponseDto(Long id, String title, String description, String imageUrl, Double price,
                                 CategoryResponseDto category, Date createdAt, Date updatedAt) {
    public static ProductResponseDto from(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getTitle(),
                product.getDescription(),
                product.getImageUrl(),
                product.getPrice(),
                product.getCategory() == null ? null : CategoryResponseDto.from(product.getCategory()),
                product.getCreatedAt(),
                product.getUpdatedAt());
    }
}
