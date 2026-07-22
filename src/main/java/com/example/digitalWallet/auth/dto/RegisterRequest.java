package com.example.digitalWallet.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** ข้อมูลที่ client ส่งมาตอนสมัครสมาชิก (record = คลาสเก็บข้อมูลแบบสั้น อ่าน-only) */
public record RegisterRequest(
        @NotBlank String username,
        @NotBlank @Size(min = 6, message = "password ต้องยาวอย่างน้อย 6 ตัว") String password
) {
}
