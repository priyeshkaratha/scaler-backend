package com.scaler.productcatalog.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDto {
    private String email;
    private String firstName;
    private String lastName;
    private List<RoleDto> roles = List.of();
}
