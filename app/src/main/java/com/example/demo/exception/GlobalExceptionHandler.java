package com.example.demo.exception;

import com.example.demo.dto.response.ErrorResponse;
import com.example.demo.dto.util.ErrorDetail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status code", HttpStatus.BAD_REQUEST.value());
        responseBody.put("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
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
