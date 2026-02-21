package com.scaler.productcatalog.controllers;

import com.scaler.productcatalog.dtos.LoginRequestDto;
import com.scaler.productcatalog.dtos.UserDto;
import com.scaler.productcatalog.dtos.SignUpRequestDto;
import com.scaler.productcatalog.exceptions.UserAlreadyExistsException;
import com.scaler.productcatalog.exceptions.UserNotFoundException;
import com.scaler.productcatalog.models.User;
import com.scaler.productcatalog.services.AuthService;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto userDto) {

        User user = authService.register(
                userDto.getEmail(),
                userDto.getPassword(),
                userDto.getFirstName(),
                userDto.getLastName());

        UserDto response = new UserDto();
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto userDto){
        Pair<User, String> pair = authService.login(userDto.getEmail(), userDto.getPassword());
        User user = pair.a;
        String token = pair.b;
        UserDto response = new UserDto();
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, token);
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }
}
