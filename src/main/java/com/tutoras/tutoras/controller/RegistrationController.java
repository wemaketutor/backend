package com.tutoras.tutoras.controller;

import org.springframework.web.bind.annotation.RestController;

import com.tutoras.tutoras.entity.Role;
import com.tutoras.tutoras.model.RegistrationRequest;
import com.tutoras.tutoras.model.RegistrationResponse;
import com.tutoras.tutoras.service.RegistrationService;

import lombok.RequiredArgsConstructor;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
public class RegistrationController extends BaseController {

    private final RegistrationService registrationService;

    @PostMapping("/auth/register")
    public ResponseEntity<RegistrationResponse> registration(@RequestBody @Validated RegistrationRequest request) {        
        return ResponseEntity.status(HttpStatus.CREATED).body(registrationService.attemptRegistration(request.getEmail(), request.getPassword(), Role.fromString(request.getRole())));
    }
    
}
