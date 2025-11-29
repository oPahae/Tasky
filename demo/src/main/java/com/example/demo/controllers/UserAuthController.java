package com.example.demo.controllers;

import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*") 
public class UserAuthController {

    @Autowired
    private UserRepository userRepository;

    // ---------------------- REGISTER -------------------------
    @PostMapping("/register")
public String register(@RequestBody User user) {
    try {
        // Validation
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return "{\"error\": \"Email requis\"}";
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return "{\"error\": \"Mot de passe requis\"}";
        }

        // Vérifier si email existe déjà
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return "{\"error\": \"Email déjà utilisé\"}";
        }

        // DÉFINIR LA DATE ICI
        user.setDateCreation(LocalDate.now());
        
        // S'assurer que disponibilite est true
        user.setDisponibilite(true);

        // Sauvegarder
        userRepository.save(user);

        return "{\"success\": true, \"message\": \"Utilisateur enregistré avec succès\"}";
        
    } catch (Exception e) {
        e.printStackTrace();
        return "{\"error\": \"" + e.getMessage() + "\"}";
    }
}

    // ---------------------- LOGIN -------------------------
    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> body, HttpSession session) {
        try {
            String email = body.get("email");
            String password = body.get("password");

            User user = userRepository.findByEmail(email);

            if (user == null) {
                return "{\"error\": \"Email incorrect\"}";
            }

            if (!password.equals(user.getPassword())) {
                return "{\"error\": \"Mot de passe incorrect\"}";
            }

            session.setAttribute("userId", user.getId());
            return "{\"success\": true, \"token\": \"session-active\", \"message\": \"Connexion réussie\"}";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    // ---------------------- LOGOUT -------------------------
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "{\"success\": true, \"message\": \"Déconnexion réussie\"}";
    }

    // ---------------------- CHECK SESSION -------------------------
    @GetMapping("/session")
    public Object checkSession(HttpSession session) {
        Object userId = session.getAttribute("userId");
        if (userId == null) {
            return "{\"error\": \"Aucun utilisateur connecté\"}";
        }
        return userRepository.findById((int) userId);
    }
}