package com.example.demo.controllers;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    //Récupération du profil
    @GetMapping("/{userId}")
    public ResponseEntity<String> getUserProfile(@PathVariable Integer userId) {
        try {
            System.out.println("=== GET USER PROFILE ===");
            System.out.println("User ID: " + userId);
            
            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest()
                    .body("{\"error\": \"ID utilisateur invalide\"}");
            }
            
            User user = userRepository.findById(userId).orElse(null);
            
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Utilisateur non trouvé\"}");
            }
            
            System.out.println("✓ Utilisateur trouvé: " + user.getEmail());
            
            String response = "{"
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
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\": \"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    //Modification du profil
    @PutMapping("/modifier/{userId}")
    public ResponseEntity<String> updateUserProfile(
            @PathVariable Integer userId,
            @RequestBody Map<String, Object> body) {
        try {
            System.out.println("=== UPDATE USER PROFILE ===");
            System.out.println("User ID: " + userId);
            
            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest()
                    .body("{\"error\": \"ID utilisateur invalide\"}");
            }
            
            User user = userRepository.findById(userId).orElse(null);
            
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Utilisateur non trouvé\"}");
            }
            
            // Mise à jour des champs si présents
            if (body.containsKey("nom") && body.get("nom") != null) {
                user.setNom((String) body.get("nom"));
            }
            if (body.containsKey("prenom") && body.get("prenom") != null) {
                user.setPrenom((String) body.get("prenom"));
            }
            if (body.containsKey("email") && body.get("email") != null) {
                String newEmail = (String) body.get("email");
                
                // Vérifier si l'email est déjà utilisé par un autre utilisateur
                User existingUser = userRepository.findByEmail(newEmail);
                if (existingUser != null && existingUser.getId() != user.getId()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("{\"error\": \"Cet email est déjà utilisé\"}");
                }
                user.setEmail(newEmail);
            }
            if (body.containsKey("competance") && body.get("competance") != null) {
                user.setCompetance((String) body.get("competance"));
            }
            if (body.containsKey("telephone") && body.get("telephone") != null) {
                user.setTelephone((String) body.get("telephone"));
            }
            if (body.containsKey("disponibilite")) {
                user.setDisponibilite((Boolean) body.get("disponibilite"));
            }
            
            userRepository.save(user);
            
            System.out.println("✓ Profil mis à jour: " + user.getEmail());
            
            return ResponseEntity.ok(
                "{\"success\": true, \"message\": \"Profil mis à jour avec succès\"}");
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\": \"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    //Modification du mot de passe
    @PutMapping("/password/{userId}")
    public ResponseEntity<String> changePassword(
            @PathVariable Integer userId,
            @RequestBody Map<String, String> body) {
        try {
            System.out.println("=== CHANGE PASSWORD ===");
            System.out.println("User ID: " + userId);
            
            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest()
                    .body("{\"error\": \"ID utilisateur invalide\"}");
            }
            
            String currentPassword = body.get("currentPassword");
            String newPassword = body.get("newPassword");
            
            if (currentPassword == null || currentPassword.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("{\"error\": \"Mot de passe actuel manquant\"}");
            }
            
            if (newPassword == null || newPassword.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("{\"error\": \"Nouveau mot de passe manquant\"}");
            }
            
            if (newPassword.length() < 6) {
                return ResponseEntity.badRequest()
                    .body("{\"error\": \"Le nouveau mot de passe doit contenir au moins 6 caractères\"}");
            }
            
            User user = userRepository.findById(userId).orElse(null);
            
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Utilisateur non trouvé\"}");
            }
            
            // Vérifier le mot de passe actuel
            if (!user.getPassword().equals(currentPassword)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"Mot de passe actuel incorrect\"}");
            }
            
            // Mettre à jour le mot de passe
            user.setPassword(newPassword);
            userRepository.save(user);
            
            System.out.println("✓ Mot de passe changé pour: " + user.getEmail());
            
            return ResponseEntity.ok(
                "{\"success\": true, \"message\": \"Mot de passe modifié avec succès\"}");
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\": \"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    
     //Suppression du compte
     
     
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteUserAccount(@PathVariable Integer userId) {
        try {
            System.out.println("=== DELETE USER ACCOUNT ===");
            System.out.println("User ID: " + userId);
            
            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest()
                    .body("{\"error\": \"ID utilisateur invalide\"}");
            }
            
            User user = userRepository.findById(userId).orElse(null);
            
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Utilisateur non trouvé\"}");
            }
            
            String userEmail = user.getEmail();
            
            // Supprimer l'utilisateur
            userRepository.delete(user);
            
            System.out.println("✓ Compte supprimé: " + userEmail);
            
            return ResponseEntity.ok(
                "{\"success\": true, \"message\": \"Compte supprimé avec succès\"}");
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\": \"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    
      //Méthode utilitaire pour échapper les caractères spéciaux JSON
     
    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}
