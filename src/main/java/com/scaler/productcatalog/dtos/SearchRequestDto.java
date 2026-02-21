package com.scaler.productcatalog.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchRequestDto {
    private String query;
    private String category;
    private Double minPrice;
    private Double maxPrice;
    private int pageSize = 10;
    private int pageNumber = 0;
    private String sortBy;
    private String sortDirection = "asc";
}
