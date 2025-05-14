package com.tutoras.tutoras;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.tutoras.tutoras.model.ConflictErrorResponse;
import com.tutoras.tutoras.model.MessageErrorResponse;
import com.tutoras.tutoras.model.UnauthorizeErrorResponse;
import com.tutoras.tutoras.model.ValidationErrorResponse;
import com.tutoras.tutoras.error.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<MessageErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        MessageErrorResponse error = new MessageErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationError(ValidationException ex) {
        ValidationErrorResponse error = new ValidationErrorResponse(ex.getField(), ex.getDetail());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ConflictErrorResponse> handleConflictError(ConflictException ex) {
        ConflictErrorResponse error = new ConflictErrorResponse(ex.getDetail());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<UnauthorizeErrorResponse> handleUnauthorizeError(UnauthorizedException ex) {
        UnauthorizeErrorResponse error = new UnauthorizeErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
}
