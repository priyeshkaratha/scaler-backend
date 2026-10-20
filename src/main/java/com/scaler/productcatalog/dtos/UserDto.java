package com.scaler.productcatalog.dtos;

import com.scaler.productcatalog.models.Role;
import com.scaler.productcatalog.models.User;
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

    public static UserDto from(User user) {
        UserDto dto = new UserDto();
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRoles(user.getRoles().stream().map(Role::getValue).map(RoleDto::new).toList());
        return dto;
    }
}
