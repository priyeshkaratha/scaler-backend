package com.scaler.productcatalog.dtos;

import com.scaler.productcatalog.models.Category;

public record CategoryResponseDto(Long id, String title) {
    public static CategoryResponseDto from(Category category) {
        return new CategoryResponseDto(category.getId(), category.getTitle());
    }
}
