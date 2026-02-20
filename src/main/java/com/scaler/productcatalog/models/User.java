package com.scaler.productcatalog.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class User extends BaseModel{
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
