package com.tutoras.tutoras.controller;

import org.springframework.web.bind.annotation.RestController;
import com.tutoras.tutoras.model.LoginRequest;
import com.tutoras.tutoras.model.LoginResponse;
import com.tutoras.tutoras.model.ProfileRequest;
import com.tutoras.tutoras.model.UserResponse;
import com.tutoras.tutoras.security.UserPrincipal;
import com.tutoras.tutoras.service.AuthService;
import com.tutoras.tutoras.service.ProfileService;
import com.tutoras.tutoras.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final AuthService authService;
    private final UserService userService;
    private final ProfileService profileService;

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Validated LoginRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.attemptLogin(request.getEmail(), request.getPassword()));
    }

    @GetMapping("/auth/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        authService.attemptLogout(request, response);
    }
    
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getProfile(principal.getEmail()));
    }

    @PutMapping("/profile/update")
    public ResponseEntity<?> updateProfile(
        @AuthenticationPrincipal UserPrincipal principal, 
        @ModelAttribute ProfileRequest request) {
        return profileService.updateProfile(
            principal.getUserId(), 
            request.getFirstName(), 
            request.getLastName(), 
            request.getExtraInfo(), 
            request.getAvatar());
    }    
}
