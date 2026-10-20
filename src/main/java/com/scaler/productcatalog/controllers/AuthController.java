package com.scaler.productcatalog.controllers;

import com.scaler.productcatalog.dtos.LoginRequestDto;
import com.scaler.productcatalog.dtos.SignUpRequestDto;
import com.scaler.productcatalog.dtos.UserDto;
import com.scaler.productcatalog.dtos.ValidateTokenRequestDto;
import com.scaler.productcatalog.services.AuthService;
import com.scaler.productcatalog.services.LoginResult;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@Valid @RequestBody SignUpRequestDto userDto) {
        return new ResponseEntity<>(UserDto.from(authService.register(
                userDto.getEmail(),
                userDto.getPassword(),
                userDto.getFirstName(),
                userDto.getLastName())), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody LoginRequestDto userDto){
        LoginResult result = authService.login(userDto.getEmail(), userDto.getPassword());
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + result.token());
        return new ResponseEntity<>(UserDto.from(result.user()), headers, HttpStatus.OK);
    }

    @PostMapping("/validate")
    public ResponseEntity<Void> validateToken(@Valid @RequestBody ValidateTokenRequestDto request){
        authService.validateToken(request.getToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        authService.logout(authorization.substring("Bearer ".length()).trim());
        return ResponseEntity.noContent().build();
    }
}
