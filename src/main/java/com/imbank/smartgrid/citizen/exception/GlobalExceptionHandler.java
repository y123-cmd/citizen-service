package com.imbank.smartgrid.citizen.exception;

import com.imbank.smartgrid.citizen.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CitizenNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleCitizenNotFoundException(CitizenNotFoundException ex) {
        log.warn("Citizen not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(404, ex.getMessage(), null));
    }

    @ExceptionHandler(InvalidCitizenException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCitizenException(InvalidCitizenException ex) {
        log.warn("Invalid citizen data: field={}, rejectedValue={}, reason={}",
                ex.getFieldName(), ex.getRejectedValue(), ex.getReason());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(400, ex.getMessage(), null));
    }

    @ExceptionHandler(ProviderServiceUnavailableException.class)
    public ResponseEntity<ApiResponse<Void>> handleProviderServiceUnavailableException(ProviderServiceUnavailableException ex) {
        log.error("Provider service unavailable: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ApiResponse<>(503, ex.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        log.warn("Validation failed: {}", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(400, "Validation failed", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(500, "An unexpected error occurred", null));
    }
}