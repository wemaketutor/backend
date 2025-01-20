package com.tutoras.tutoras.controller;

import org.springframework.web.bind.annotation.RestController;

import com.tutoras.tutoras.model.RegistrationRequest;
import com.tutoras.tutoras.service.RegistrationService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody @Validated RegistrationRequest request) {        
        return registrationService.attemptRegistration(request.getEmail(), request.getPassword(), request.getRole());
    }
    
}
