package com.example.digitalWallet.auth.dto;

import jakarta.validation.constraints.NotBlank;

/** ข้อมูลที่ client ส่งมาตอน login */
public record LoginRequest(
        @NotBlank String username,
        @NotBlank String password
) {
}
