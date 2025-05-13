package com.tutoras.tutoras.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tutoras.tutoras.model.LoginResponse;
import com.tutoras.tutoras.model.ValidationErrorResponse;
import com.tutoras.tutoras.security.JwtIssuer;
import com.tutoras.tutoras.security.UserPrincipal;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtIssuer jwtIssuer;
    private final AuthenticationManager authenticationManager;

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email != null && email.matches(emailRegex);
    }    

    public ResponseEntity<?> attemptLogin(String email, String password) {
        if (!isValidEmail(email)) {
            ValidationErrorResponse errorResponse = new ValidationErrorResponse("email", "The mail is incorrect");
            return ResponseEntity.unprocessableEntity().body(errorResponse);
        }
        var authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        var principal = (UserPrincipal) authentication.getPrincipal();

        var roles = principal.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .toList();

        var token = jwtIssuer.issue(principal.getUserId(), principal.getEmail(), roles);
        return ResponseEntity.status(200).body(
            LoginResponse.builder()
            .accessToken(token)
            .build()
        );
    }
}
