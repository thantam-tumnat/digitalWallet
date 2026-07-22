package com.example.digitalWallet.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * สร้างและตรวจสอบ JWT
 * JWT = ป้ายที่ server เซ็นชื่อกำกับ (ด้วย secret) แล้วส่งให้ client เก็บไว้
 * รอบหน้า client แนบป้ายนี้มา server แค่ตรวจลายเซ็นก็รู้ว่าใคร โดยไม่ต้องเก็บ session
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /** สร้าง token ใหม่ให้ผู้ใช้ */
    public String generateToken(String username, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)                 // เจ้าของ token คือใคร
                .claim("role", role)               // แนบ role ไปด้วย
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(key)                     // เซ็นลายเซ็นด้วย secret
                .compact();
    }

    /** ดึง username ออกจาก token */
    public String extractUsername(String token) {
        return parse(token).getSubject();
    }

    /** true = token ถูกต้องและยังไม่หมดอายุ */
    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
