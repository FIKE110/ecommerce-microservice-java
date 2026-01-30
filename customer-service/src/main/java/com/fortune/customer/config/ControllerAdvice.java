package com.fortune.customer.config;

import com.fortune.customer.exception.ProfileNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<String> ProfileNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile not found");
    }
}
