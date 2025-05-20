package com.tutoras.tutoras.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentInResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
} 