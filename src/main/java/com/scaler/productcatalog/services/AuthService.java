package com.scaler.productcatalog.services;

import com.scaler.productcatalog.exceptions.UserNotFoundException;
import com.scaler.productcatalog.models.User;
import org.antlr.v4.runtime.misc.Pair;

public interface AuthService {
    Pair<User, String> login(String email, String password) throws UserNotFoundException;
    User register(String email, String password, String firstName, String lastName) throws UserNotFoundException;
}
