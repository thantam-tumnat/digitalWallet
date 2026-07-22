package com.example.digitalWallet.auth;

import com.example.digitalWallet.auth.dto.AuthResponse;
import com.example.digitalWallet.auth.dto.LoginRequest;
import com.example.digitalWallet.auth.dto.RegisterRequest;
import com.example.digitalWallet.security.JwtService;
import com.example.digitalWallet.user.Role;
import com.example.digitalWallet.user.User;
import com.example.digitalWallet.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** ตรรกะหลักของการสมัครสมาชิกและ login */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /** สมัครสมาชิก: เก็บ username + password (แฮชแล้ว) */
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "username นี้ถูกใช้แล้ว");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password())); // แฮชก่อนเก็บเสมอ
        user.setRole(Role.USER);
        userRepository.save(user);
    }

    /** login: เช็ค password แล้วออก JWT ให้ */
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username หรือ password ไม่ถูกต้อง"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username หรือ password ไม่ถูกต้อง");
        }

        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token);
    }
}
