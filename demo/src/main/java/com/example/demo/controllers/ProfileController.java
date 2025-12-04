package com.example.demo.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/info")
    public String getUserInfo(@RequestBody Map<String, String> body) {
        try {
            String token = body.get("token");
            
            System.out.println("=== GET USER INFO ===");
            System.out.println("Token: " + token);
            
            if (token == null || token.isEmpty()) {
                return "{\"error\": \"Token manquant\"}";
            }
            
            Integer userId = UserAuthController.getUserIdFromToken(token);
            
            if (userId == -1) {
                return "{\"error\": \"Token invalide ou expiré\"}";
            }
            
            User user = userRepository.findById(userId).orElse(null);
            
            if (user == null) {
                return "{\"error\": \"Utilisateur non trouvé\"}";
            }
            
            System.out.println("✓ Utilisateur trouvé: " + user.getEmail());
            
            return "{"
                    + "\"success\": true, "
                    + "\"id\": " + user.getId() + ", "
                    + "\"nom\": \"" + escapeJson(user.getNom()) + "\", "
                    + "\"prenom\": \"" + escapeJson(user.getPrenom()) + "\", "
                    + "\"email\": \"" + escapeJson(user.getEmail()) + "\", "
                    + "\"competance\": \"" + escapeJson(user.getCompetance()) + "\", "
                    + "\"telephone\": \"" + escapeJson(user.getTelephone()) + "\", "
                    + "\"disponibilite\": " + user.isDisponibilite() + ", "
                    + "\"dateCreation\": \"" + user.getDateCreation() + "\""
                    + "}";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    @PostMapping("/update")
    public String updateUser(@RequestBody Map<String, Object> body) {
        try {
            String token = (String) body.get("token");
            
            System.out.println("=== UPDATE USER INFO ===");
            System.out.println("Token: " + token);
            
            if (token == null || token.isEmpty()) {
                return "{\"error\": \"Token manquant\"}";
            }
            
            Integer userId = UserAuthController.getUserIdFromToken(token);
            
            if (userId == -1) {
                return "{\"error\": \"Token invalide ou expiré\"}";
            }
            
            User user = userRepository.findById(userId).orElse(null);
            
            if (user == null) {
                return "{\"error\": \"Utilisateur non trouvé\"}";
            }
            
            if (body.containsKey("nom")) {
                user.setNom((String) body.get("nom"));
            }
            if (body.containsKey("prenom")) {
                user.setPrenom((String) body.get("prenom"));
            }
            if (body.containsKey("email")) {
                String newEmail = (String) body.get("email");
                User existingUser = userRepository.findByEmail(newEmail);
                if (existingUser != null && existingUser.getId() != user.getId()) {
                    return "{\"error\": \"Cet email est déjà utilisé\"}";
                }
                user.setEmail(newEmail);
            }
            if (body.containsKey("competance")) {
                user.setCompetance((String) body.get("competance"));
            }
            if (body.containsKey("telephone")) {
                user.setTelephone((String) body.get("telephone"));
            }
            if (body.containsKey("disponibilite")) {
                user.setDisponibilite((Boolean) body.get("disponibilite"));
            }
            
            userRepository.save(user);
            
            System.out.println("✓ Utilisateur mis à jour: " + user.getEmail());
            
            return "{\"success\": true, \"message\": \"Informations mises à jour avec succès\"}";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestBody Map<String, String> body) {
        try {
            String token = body.get("token");
            String currentPassword = body.get("currentPassword");
            String newPassword = body.get("newPassword");
            
            System.out.println("=== CHANGE PASSWORD ===");
            
            if (token == null || token.isEmpty()) {
                return "{\"error\": \"Token manquant\"}";
            }
            
            if (currentPassword == null || newPassword == null) {
                return "{\"error\": \"Mots de passe manquants\"}";
            }
            
            if (newPassword.length() < 6) {
                return "{\"error\": \"Le nouveau mot de passe doit contenir au moins 6 caractères\"}";
            }
            
            Integer userId = UserAuthController.getUserIdFromToken(token);
            
            if (userId == -1) {
                return "{\"error\": \"Token invalide ou expiré\"}";
            }
            
            User user = userRepository.findById(userId).orElse(null);
            
            if (user == null) {
                return "{\"error\": \"Utilisateur non trouvé\"}";
            }
            
            if (!user.getPassword().equals(currentPassword)) {
                return "{\"error\": \"Mot de passe actuel incorrect\"}";
            }
            
            user.setPassword(newPassword);
            userRepository.save(user);
            
            System.out.println("✓ Mot de passe changé pour: " + user.getEmail());
            
            return "{\"success\": true, \"message\": \"Mot de passe modifié avec succès\"}";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}