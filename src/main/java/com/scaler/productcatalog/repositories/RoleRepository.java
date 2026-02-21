package com.scaler.productcatalog.repositories;

import com.scaler.productcatalog.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByValue(String value);
    Role save(Role role);
}
