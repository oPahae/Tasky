package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.jwt.JwtUtil;
import com.example.demo.models.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired 
    private UserRepository repo;

    @Autowired 
    private PasswordEncoder encoder;

    @Autowired 
    private JwtUtil jwtUtil;

    @Autowired 
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {

        if (repo.existsByEmail(req.email())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User user = new User();
        user.setEmail(req.email());
        user.setPassword(encoder.encode(req.password()));

        repo.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {

        Authentication auth = new UsernamePasswordAuthenticationToken(
                req.email(),
                req.password()
        );

        authenticationManager.authenticate(auth);

        String token = jwtUtil.generateToken(req.email());

        return ResponseEntity.ok(new AuthResponse(token));
    }
}
