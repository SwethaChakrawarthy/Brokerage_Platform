package portfolio_service.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import portfolio_service.model.Role;
import portfolio_service.model.User;
import portfolio_service.repository.UserRepository;
import portfolio_service.security.JwtUtil;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // POST /api/v1/auth/register
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(Role.valueOf(request.getRole()))
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user);
        return ResponseEntity.ok(
                new AuthResponse(token, user.getEmail(),
                        user.getRole().name()));
    }

    // POST /api/v1/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));

        User user = (User) auth.getPrincipal();
        String token = jwtUtil.generateToken(user);

        return ResponseEntity.ok(
                new AuthResponse(token, user.getEmail(),
                        user.getRole().name()));
    }

    @Data
    static class LoginRequest {
        private String email;
        private String password;
    }

    @Data
    static class RegisterRequest {
        private String email;
        private String password;
        private String fullName;
        private String role;
    }

    @Data
    static class AuthResponse {
        private final String token;
        private final String email;
        private final String role;
    }
}