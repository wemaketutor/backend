package com.tutoras.tutoras.exception;

import java.util.NoSuchElementException;

public class NotFindedSuchElementException extends NoSuchElementException {
    
    private final String message;

    public NotFindedSuchElementException(String message){
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
