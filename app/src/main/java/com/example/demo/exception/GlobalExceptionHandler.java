package com.example.demo.exception;

import com.example.demo.dto.response.ErrorResponse;
import com.example.demo.dto.util.ErrorDetail;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        errors.put("status code", HttpStatus.BAD_REQUEST.value());
        errors.put("errors", fieldErrors);

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorDetail errorDetail = new ErrorDetail(new Timestamp(System.currentTimeMillis()), 400, ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(Collections.singletonList(errorDetail));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorDetail> handleResponseStatusException(ResponseStatusException ex) {
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setTimestamp(new Timestamp(System.currentTimeMillis()));
        errorDetail.setCode(HttpStatus.UNAUTHORIZED.value());
        errorDetail.setDetail(ex.getReason());
        return new ResponseEntity<>(errorDetail, HttpStatus.UNAUTHORIZED);
    }
}
