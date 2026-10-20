package com.scaler.productcatalog.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Role extends BaseModel {
    public static final String ADMIN = "ADMIN";
    public static final String NON_ADMIN_USER = "NON_ADMIN_USER";

    @Column(nullable = false, unique = true)
    private String value;
}
