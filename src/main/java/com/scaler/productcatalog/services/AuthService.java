package com.scaler.productcatalog.services;

import com.scaler.productcatalog.exceptions.UserNotFoundException;
import com.scaler.productcatalog.models.User;

public interface AuthService {
    User login(String email, String password) throws UserNotFoundException;
    User register(String email, String password, String firstName, String lastName) throws UserNotFoundException;
}
