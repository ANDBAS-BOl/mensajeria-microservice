package com.pragma.powerup.mensajeria.infrastructure.exceptionhandler;

import com.pragma.powerup.mensajeria.domain.exception.BusinessRuleException;
import com.pragma.powerup.mensajeria.domain.exception.DomainException;
import com.pragma.powerup.mensajeria.domain.exception.InternalProcessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerAdvisor {

    private static final String MESSAGE = "message";
    private static final String ERRORS = "errors";
    private static final String VALIDATION_FAILED = "Validation failed";
    private static final String INVALID_REQUEST_BODY = "Invalid request body";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(fieldError -> errors.put(fieldError.getField(), fieldError.getDefaultMessage()));
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(MESSAGE, VALIDATION_FAILED);
        body.put(ERRORS, errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            String field = propertyPath.contains(".")
                    ? propertyPath.substring(propertyPath.lastIndexOf('.') + 1)
                    : propertyPath;
            errors.put(field, violation.getMessage());
        });
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(MESSAGE, VALIDATION_FAILED);
        body.put(ERRORS, errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleMessageNotReadableException(HttpMessageNotReadableException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(MESSAGE, INVALID_REQUEST_BODY));
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<Map<String, String>> handleBusinessRuleException(BusinessRuleException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(MESSAGE, exception.getMessage()));
    }

    @ExceptionHandler(InternalProcessException.class)
    public ResponseEntity<Map<String, String>> handleInternalProcessException(InternalProcessException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(MESSAGE, exception.getMessage()));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String, String>> handleDomainException(DomainException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(MESSAGE, exception.getMessage()));
    }
}