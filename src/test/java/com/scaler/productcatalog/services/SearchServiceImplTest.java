package com.scaler.productcatalog.services;

import com.scaler.productcatalog.dtos.SearchRequestDto;
import com.scaler.productcatalog.exceptions.InvalidRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SearchServiceImplTest {
    private final SearchServiceImpl searchService = new SearchServiceImpl();

    @Test
    void unknownSortFieldFallsBackToId() {
        SearchRequestDto request = new SearchRequestDto();
        request.setSortBy("price; DROP TABLE product");
        Pageable pageable = searchService.buildPageable(request);
        assertThat(pageable.getSort().getOrderFor("id")).isNotNull();
    }

    @Test
    void missingSortFieldDefaultsToIdAscending() {
        Pageable pageable = searchService.buildPageable(new SearchRequestDto());
        assertThat(pageable.getSort().getOrderFor("id").getDirection()).isEqualTo(Sort.Direction.ASC);
        assertThat(pageable.getPageSize()).isEqualTo(10);
    }

    @Test
    void pageSizeIsCappedAndDirectionParsed() {
        SearchRequestDto request = new SearchRequestDto();
        request.setPageSize(1000);
        request.setSortBy("price");
        request.setSortDirection("DESC");
        Pageable pageable = searchService.buildPageable(request);
        assertThat(pageable.getPageSize()).isEqualTo(100);
        assertThat(pageable.getSort().getOrderFor("price").getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void rejectsInvertedPriceRange() {
        SearchRequestDto request = new SearchRequestDto();
        request.setMinPrice(500.0);
        request.setMaxPrice(100.0);
        assertThatThrownBy(() -> searchService.searchProducts(request)).isInstanceOf(InvalidRequestException.class);
    }
}
