package com.tutoras.tutoras.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MessageErrorResponse {
    private final String message;
}
