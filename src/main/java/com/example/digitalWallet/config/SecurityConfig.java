package com.example.digitalWallet.config;

import com.example.digitalWallet.security.JwtAuthenticationFilter;
import com.example.digitalWallet.security.JwtService;
import com.example.digitalWallet.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * ตั้งค่าความปลอดภัยของทั้งแอป
 * - ปิด session (STATELESS) เพราะเราใช้ JWT แทน
 * - /api/auth/** เข้าได้โดยไม่ต้อง login (register/login), ที่เหลือต้องมี token
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public SecurityConfig(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ปิด CSRF เพราะเป็น REST API แบบ stateless (ไม่ใช้ cookie session)
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // ถ้าไม่มี token / token ไม่ถูก -> ตอบ 401 (ไม่ใช่ 403) เป็น JSON
                .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter().write("{\"status\":401,\"message\":\"ต้องเข้าสู่ระบบก่อน (unauthorized)\"}");
                }))
                // สร้าง filter ตรวจ JWT ด้วย new เอง แล้ววางไว้ใน security chain ที่เดียว
                // (ไม่ทำเป็น @Component เพื่อไม่ให้ Boot ลงทะเบียนซ้ำใน servlet chain)
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtService, userRepository),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /** ตัวเข้ารหัส password แบบ BCrypt (แฮชทางเดียว ถอดกลับไม่ได้) */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
