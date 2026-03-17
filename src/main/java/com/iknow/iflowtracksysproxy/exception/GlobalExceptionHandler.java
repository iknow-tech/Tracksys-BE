package com.iknow.iflowtracksysproxy.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("GlobalExceptionHandler caught IllegalArgumentException", ex);
        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "success", false,
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {
        log.error("GlobalExceptionHandler caught unexpected exception", ex);
        return ResponseEntity
                .internalServerError()
                .body(Map.of(
                        "success", false,
                        "message", "Beklenmeyen bir hata oluştu"
                ));
    }
}
