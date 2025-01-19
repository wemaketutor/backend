package com.tutoras.tutoras.model;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class RegistrationRequest {
    private String email;
    private String password;
    private String role;
}
