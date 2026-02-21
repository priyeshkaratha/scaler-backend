package com.scaler.productcatalog.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Product extends BaseModel {
    private String title;
    private String description;
    private String imageUrl;
    private Double price;

    @ManyToOne
    private Category category;
}
