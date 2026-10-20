package com.scaler.productcatalog.dtos;

import com.scaler.productcatalog.models.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCreateRequestDto {
    @NotBlank
    @Size(max = 255)
    private String title;

    @Size(max = 2000)
    private String description;

    @NotNull
    @PositiveOrZero
    private Double price;

    @NotBlank
    private String category;

    @Size(max = 1000)
    private String imageUrl;

    public Product getProduct(){
        Product product = new Product();
        product.setTitle(title);
        product.setDescription(description);
        product.setImageUrl(imageUrl);
        product.setPrice(price);
        return product;
    }
}
