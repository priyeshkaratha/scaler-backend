package com.scaler.productcatalog.services;

import com.scaler.productcatalog.models.User;

public record LoginResult(User user, String token) {
}
