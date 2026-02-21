package com.scaler.productcatalog.controllers;

import com.scaler.productcatalog.dtos.SearchRequestDto;
import com.scaler.productcatalog.models.Product;
import com.scaler.productcatalog.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @PostMapping
    public ResponseEntity<Page<Product>> search(@RequestBody SearchRequestDto searchRequestDto){
        Page<Product> products = searchService.searchProducts(searchRequestDto);
        return ResponseEntity.ok(products);
    }
}
