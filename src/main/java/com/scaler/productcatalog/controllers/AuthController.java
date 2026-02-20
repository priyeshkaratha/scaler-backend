package com.scaler.productcatalog.controllers;

import com.scaler.productcatalog.dtos.UserLoginRequestDto;
import com.scaler.productcatalog.dtos.UserLoginResponseDto;
import com.scaler.productcatalog.dtos.UserSignUpRequestDto;
import com.scaler.productcatalog.dtos.UserSignUpResponseDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/signup")
    public UserSignUpResponseDto signUp(@RequestBody UserSignUpRequestDto userDto ){
        return new UserSignUpResponseDto();
    }

    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody UserLoginRequestDto userDto){
        return new UserLoginResponseDto();
    }
}
