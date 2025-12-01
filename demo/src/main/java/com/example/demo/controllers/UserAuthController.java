package com.example.demo.controllers;

import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class UserAuthController {

    @Autowired
    private UserRepository userRepository;

    // Stockage des tokens actifs (en mémoire)
    private static final ConcurrentHashMap<String, Integer> activeTokens = new ConcurrentHashMap<>();

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

            // Définir la date
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
    public String login(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            String password = body.get("password");

            System.out.println("=== TENTATIVE DE CONNEXION ===");
            System.out.println("Email: " + email);

            User user = userRepository.findByEmail(email);

            if (user == null) {
                return "{\"error\": \"Email incorrect\"}";
            }

            if (!password.equals(user.getPassword())) {
                return "{\"error\": \"Mot de passe incorrect\"}";
            }

            // Générer un token unique
            String token = UUID.randomUUID().toString();
            activeTokens.put(token, user.getId());

            System.out.println("Token généré: " + token);
            System.out.println("User ID: " + user.getId());
            System.out.println("Tokens actifs: " + activeTokens.size());
            System.out.println("=== CONNEXION REUSSIE ===");

            // ⭐ MODIFICATION : Retourner TOUTES les informations utilisateur
            return "{"
                    + "\"success\": true, "
                    + "\"token\": \"" + token + "\", "
                    + "\"message\": \"Connexion réussie\", "
                    + "\"id\": " + user.getId() + ", "
                    + "\"nom\": \"" + escapeJson(user.getNom()) + "\", "
                    + "\"prenom\": \"" + escapeJson(user.getPrenom()) + "\", "
                    + "\"email\": \"" + escapeJson(user.getEmail()) + "\", "
                    + "\"competance\": \"" + escapeJson(user.getCompetance()) + "\", "
                    + "\"telephone\": \"" + escapeJson(user.getTelephone()) + "\""
                    + "}";

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    // ---------------------- VERIFY TOKEN -------------------------
    @PostMapping("/verify")
    public String verifyToken(@RequestBody Map<String, String> body) {
        try {
            String token = body.get("token");

            System.out.println("=== VERIFICATION TOKEN ===");
            System.out.println("Token reçu: " + token);
            System.out.println("Tokens actifs: " + activeTokens.size());

            if (token == null || token.isEmpty()) {
                System.out.println("✗ Token vide");
                return "{\"error\": \"Token manquant\"}";
            }

            if (!activeTokens.containsKey(token)) {
                System.out.println("✗ Token invalide ou expiré");
                return "{\"error\": \"Token invalide ou expiré\"}";
            }

            Integer userId = activeTokens.get(token);
            User user = userRepository.findById(userId).orElse(null);

            if (user != null) {
                System.out.println("✓ Utilisateur trouvé: " + user.getEmail());

                // ⭐ MODIFICATION : Retourner toutes les infos
                return "{"
                        + "\"success\": true, "
                        + "\"id\": " + user.getId() + ", "
                        + "\"nom\": \"" + escapeJson(user.getNom()) + "\", "
                        + "\"prenom\": \"" + escapeJson(user.getPrenom()) + "\", "
                        + "\"email\": \"" + escapeJson(user.getEmail()) + "\", "
                        + "\"competance\": \"" + escapeJson(user.getCompetance()) + "\", "
                        + "\"telephone\": \"" + escapeJson(user.getTelephone()) + "\""
                        + "}";
            }

            System.out.println("✗ Utilisateur non trouvé en base");
            return "{\"error\": \"Utilisateur non trouvé\"}";

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    // ---------------------- LOGOUT -------------------------
    @PostMapping("/logout")
    public String logout(@RequestBody Map<String, String> body) {
        try {
            String token = body.get("token");

            System.out.println("=== DECONNEXION ===");
            System.out.println("Token: " + token);

            if (token != null && activeTokens.containsKey(token)) {
                activeTokens.remove(token);
                System.out.println("✓ Token supprimé");
                System.out.println("Tokens restants: " + activeTokens.size());
            }

            return "{\"success\": true, \"message\": \"Déconnexion réussie\"}";

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    // ⭐ NOUVELLE MÉTHODE : Échapper les caractères spéciaux JSON
    private String escapeJson(String value) {
        if (value == null)
            return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // ---------------------- MÉTHODES PUBLIQUES POUR UserController
    // -------------------------

    public static Integer getUserIdFromToken(String token) {
        return activeTokens.get(token);
    }

    public static void removeToken(String token) {
        activeTokens.remove(token);
        System.out.println("✓ Token supprimé: " + token);
    }

    public static boolean isValidToken(String token) {
        return activeTokens.containsKey(token);
    }

    // ---------------------- DEBUG: AFFICHER TOKENS -------------------------
    @GetMapping("/tokens/debug")
    public String debugTokens() {
        System.out.println("=== TOKENS ACTIFS ===");
        activeTokens.forEach((token, userId) -> {
            System.out.println("Token: " + token + " -> User ID: " + userId);
        });
        return "{\"activeTokens\": " + activeTokens.size() + "}";
    }

    // ---------------------- RESET PASSWORD -------------------------
    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            String newPassword = body.get("newPassword");

            System.out.println("=== REINITIALISATION MOT DE PASSE ===");
            System.out.println("Email: " + email);

            User user = userRepository.findByEmail(email);
            if (user == null) {
                return "{\"error\": \"Email non trouvé\"}";
            }

            user.setPassword(newPassword);
            userRepository.save(user);

            System.out.println("✓ Mot de passe mis à jour pour: " + email);
            return "{\"success\": true, \"message\": \"Mot de passe mis à jour avec succès\"}";

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }
}