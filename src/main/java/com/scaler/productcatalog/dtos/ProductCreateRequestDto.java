package com.scaler.productcatalog.dtos;

import com.scaler.productcatalog.models.Product;
import lombok.Getter;

@Getter
public class ProductCreateRequestDto {
    private String title;
    private String description;
    private Double price;
    private String category;
    private String imageUrl;

    public Product getProduct(){
        Product product = new Product();
        product.setTitle(title);
        product.setCreatedAt(null);
        product.setUpdatedAt(null);
        product.setIsActive(true);
        product.setDescription(description);
        product.setImageUrl(imageUrl);
        product.setPrice(price);
        return product;
    }
}
