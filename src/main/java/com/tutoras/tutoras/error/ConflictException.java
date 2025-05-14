package com.tutoras.tutoras.error;

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
