package com.tutoras.tutoras.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ValidationErrorResponse {
    private final String field;
    private final String detail;
}
