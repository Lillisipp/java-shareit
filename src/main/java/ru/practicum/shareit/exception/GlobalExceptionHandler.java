package ru.practicum.shareit.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class,
            BindException.class,
            IllegalArgumentException.class,
            MissingRequestHeaderException.class,
            MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, String>> badRequest(Exception ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    //403
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> forbidden(Exception ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", ex.getMessage()));
    }

    //404
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> notFound(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    // 409 — конфликт уникальности
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, String>> handleConflict(Exception ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }

    //500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> internal(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "internal error"));
    }

    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String m) {
            super(m);
        }
    }

    public static class ConflictException extends RuntimeException {
        public ConflictException(String m) {
            super(m);
        }
    }
}

