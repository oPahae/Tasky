// package com.example.demo.controllers;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
        
// import com.example.demo.dto.AuthResponse;
// import com.example.demo.dto.LoginRequest;
// import com.example.demo.dto.RegisterRequest;
// import com.example.demo.jwt.JwtUtil;
// import com.example.demo.repositories.UserRepository;
// import com.example.demo.models.User;

// @RestController
// @RequestMapping("/auth")
// public class AuthController {

//     @Autowired 
//     private UserRepository repo;

//     @Autowired 
//     private PasswordEncoder encoder;

//     @Autowired 
//     private JwtUtil jwtUtil;

//     @Autowired 
//     private AuthenticationManager authenticationManager;

//     // REGISTER 
//     @PostMapping("/register")
//     public ResponseEntity<?> register(@RequestBody RegisterRequest req) {

//         if (repo.existsByEmail(req.email())) {
//             return ResponseEntity.badRequest().body("Email already exists");
//         }

//         User user = new User();
//         user.setEmail(req.email());
//         user.setPassword(encoder.encode(req.password()));

//         repo.save(user);

//         return ResponseEntity.ok("User registered successfully");
//     }

//     // LOGIN
//     @PostMapping("/login")
//     public ResponseEntity<?> login(@RequestBody LoginRequest req) {

//         // Authentication object
//         Authentication auth = new UsernamePasswordAuthenticationToken(
//                 req.email(),
//                 req.password()
//         );

//         // Vérifier email + password
//         authenticationManager.authenticate(auth);

//         // Générer un token avec email
//         String token = jwtUtil.generateToken(req.email());

//         // Retourner le token dans AuthResponse
//         return ResponseEntity.ok(new AuthResponse(token));
//     }
// }
