package com.example.digitalWallet.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

/**
 * ดักจับ exception ทุกตัวจาก controller ไว้ที่เดียว แล้วแปลงเป็น JSON error สวย ๆ
 * ข้อดี: response ถูกเขียนตรงนี้เลย ไม่ต้อง forward ไป /error (เลี่ยงปัญหา 403 ทับ status จริง)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** exception ที่เราตั้งใจโยนเอง เช่น 409 username ซ้ำ, 401 login ผิด */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException ex) {
        HttpStatusCode status = ex.getStatusCode();
        return ResponseEntity.status(status).body(new ApiError(status.value(), ex.getReason()));
    }

    /** input ไม่ผ่าน validation (@NotBlank, @Size ...) -> 400 พร้อมบอกว่า field ไหนพัง */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(new ApiError(400, message));
    }
}
