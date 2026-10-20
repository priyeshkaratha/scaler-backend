package com.scaler.productcatalog.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchRequestDto {
    private String query;
    private String category;

    @PositiveOrZero
    private Double minPrice;

    @PositiveOrZero
    private Double maxPrice;

    @Min(1)
    @Max(100)
    private int pageSize = 10;

    @Min(0)
    private int pageNumber = 0;

    private String sortBy;
    private String sortDirection = "asc";
}
