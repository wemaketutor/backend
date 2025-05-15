package com.tutoras.tutoras.exception;

public class ConflictException extends RuntimeException {
    private final String detail;

    public ConflictException(String detail) {
        super(detail);
        this.detail = detail;
    }

    public String getDetail() {
        return detail;
    }
}
