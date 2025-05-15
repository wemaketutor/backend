package com.tutoras.tutoras.exception;

public class ValidationException extends RuntimeException {
    private final String field;
    private final String detail;

    public ValidationException(String field, String detail) {
        super(detail);
        this.field = field;
        this.detail = detail;
    }

    public String getField() {
        return field;
    }

    public String getDetail() {
        return detail;
    }
}
