package com.my_projects.mai_vocabulary.controller;

import com.my_projects.mai_vocabulary.dto.AuthResponse;
import com.my_projects.mai_vocabulary.dto.LoginRequest;
import com.my_projects.mai_vocabulary.dto.RegisterRequest;
import com.my_projects.mai_vocabulary.entity.User;
import com.my_projects.mai_vocabulary.service.AuthService;
import com.my_projects.mai_vocabulary.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    public AuthController(
            UserService userService,
            AuthService authService) {

        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @Valid @RequestBody RegisterRequest request) {

        userService.register(
                request.getName(),
                request.getEmail(),
                request.getPassword()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) throws Exception {

        AuthResponse response =
                authService.login(
                        request.getEmail(),
                        request.getPassword()
                );

        return ResponseEntity.ok(response);
    }
}
