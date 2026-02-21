package com.scaler.productcatalog.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SignUpRequestDto {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    List<RoleDto> roles = List.of();
}
