package com.scaler.productcatalog.services;

import com.scaler.productcatalog.models.User;

public interface AuthService {
    LoginResult login(String email, String password);
    User register(String email, String password, String firstName, String lastName);
    User registerAdmin(String email, String password);
    User validateToken(String token);
    void logout(String token);
}
