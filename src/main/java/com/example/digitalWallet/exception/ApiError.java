package com.example.digitalWallet.exception;

/** รูปแบบมาตรฐานของข้อความ error ที่ตอบกลับ client */
public record ApiError(int status, String message) {
}
