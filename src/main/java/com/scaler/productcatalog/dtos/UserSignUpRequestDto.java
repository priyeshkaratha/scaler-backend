package com.scaler.productcatalog.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignUpRequestDto {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
