package com.example.digitalWallet.auth.dto;

/** สิ่งที่ server ตอบกลับหลัง login สำเร็จ = JWT token */
public record AuthResponse(String token) {
}
