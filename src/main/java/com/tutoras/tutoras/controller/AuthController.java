package com.tutoras.tutoras.controller;

import org.springframework.web.bind.annotation.RestController;
import com.tutoras.tutoras.model.LoginRequest;
import com.tutoras.tutoras.model.LoginResponse;
import com.tutoras.tutoras.security.UserPrincipal;
import com.tutoras.tutoras.service.AuthService;
import com.tutoras.tutoras.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/api/auth/login")
    public LoginResponse login(@RequestBody @Validated LoginRequest request) {
        return authService.attemptLogin(request.getEmail(), request.getPassword());
    }

    @GetMapping("/api/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        return userService.getProfile(principal.getEmail());
    }
    
    
}
