package com.scaler.productcatalog.handlers;

import com.scaler.productcatalog.dtos.ErrorResponseDto;
import com.scaler.productcatalog.exceptions.CategoryNotFoundException;
import com.scaler.productcatalog.exceptions.ProductNotFoundException;
import com.scaler.productcatalog.exceptions.UserAlreadyExistsException;
import com.scaler.productcatalog.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleUserExists(UserAlreadyExistsException ex) {
        ErrorResponseDto response = new ErrorResponseDto();
        response.setMessage(ex.getMessage());
        response.setStatus(400);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUserNotFound(UserNotFoundException ex) {
        ErrorResponseDto response = new ErrorResponseDto();
        response.setMessage(ex.getMessage());
        response.setStatus(400);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleProductNotFound(ProductNotFoundException ex) {
        ErrorResponseDto response = new ErrorResponseDto();
        response.setMessage(ex.getMessage());
        response.setStatus(404);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleProductNotFound(CategoryNotFoundException ex) {
        ErrorResponseDto response = new ErrorResponseDto();
        response.setMessage(ex.getMessage());
        response.setStatus(404);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }
}
