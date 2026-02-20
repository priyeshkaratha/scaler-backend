package com.scaler.productcatalog.services;

import com.scaler.productcatalog.exceptions.UserNotFoundException;
import com.scaler.productcatalog.models.User;

public class AuthServiceImpl implements AuthService{
    @Override
    public User login(String email, String password) throws UserNotFoundException {
        return null;
    }

    @Override
    public User register(User user) {
        return null;
    }
}
