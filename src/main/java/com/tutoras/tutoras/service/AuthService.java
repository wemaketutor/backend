package com.tutoras.tutoras.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import com.tutoras.tutoras.model.LoginResponse;
import com.tutoras.tutoras.model.MessageErrorResponse;
import com.tutoras.tutoras.model.ValidationErrorResponse;
import com.tutoras.tutoras.security.JwtIssuer;
import com.tutoras.tutoras.security.UserPrincipal;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    public ResponseEntity<?> attemptLogout(HttpServletRequest request, HttpServletResponse response){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            MessageErrorResponse errorResponse = new MessageErrorResponse("The userId should be a number");
            return ResponseEntity.status(401).body(errorResponse);
        }
        new SecurityContextLogoutHandler().logout(request, response, auth);
        return ResponseEntity.status(200).body(null);
    }
}
