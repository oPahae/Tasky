package com.example.demo.controllers;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class UserAuthController {

    @Autowired
    private UserRepository userRepository;

    //stocker les tokens actifs et leur user
    private static final ConcurrentHashMap<String, Integer> activeTokens = new ConcurrentHashMap<>();

    //inscription du nv user
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        try {
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                return "{\"error\": \"Email requis\"}";
            }
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                return "{\"error\": \"Mot de passe requis\"}";
            }

            if (userRepository.findByEmail(user.getEmail()) != null) {
                return "{\"error\": \"Email deje utilise\"}";
            }

            user.setDateCreation(LocalDate.now());
            user.setDisponibilite(true);
            userRepository.save(user);

            return "{\"success\": true, \"message\": \"Utilisateur enregistre avec succes\"}";

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    // authetification du user et generation dyal token de session
    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            String password = body.get("password");

            System.out.println(" TENTATIVE DE CONNEXION ");
            System.out.println("Email: " + email);

            User user = userRepository.findByEmail(email);

            if (user == null) {
                return "{\"error\": \"Email incorrect\"}";
            }

            if (!password.equals(user.getPassword())) {
                return "{\"error\": \"Mot de passe incorrect\"}";
            }

            //generation du token
            String token = UUID.randomUUID().toString();
            activeTokens.put(token, user.getId());

            System.out.println("Token genere: " + token);
            System.out.println("User ID: " + user.getId());
            System.out.println("Tokens actifs: " + activeTokens.size());
            System.out.println("CONNEXION REUSSIE");

            return "{"
                    + "\"success\": true, "
                    + "\"token\": \"" + token + "\", "
                    + "\"message\": \"Connexion reussie\", "
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

    //verification si token est valide (se trouve dans la table dyal tokens) et recupere user dyal hadak token
    @PostMapping("/verify")
    public String verifyToken(@RequestBody Map<String, String> body) {
        try {
            String token = body.get("token");

            System.out.println("VERIFICATION TOKEN");
            System.out.println("Token reçu: " + token);
            System.out.println("Tokens actifs: " + activeTokens.size());

            if (token == null || token.isEmpty()) {
                System.out.println("Token vide");
                return "{\"error\": \"Token manquant\"}";
            }

            if (!activeTokens.containsKey(token)) {
                System.out.println("Token invalide ou expire");
                return "{\"error\": \"Token invalide ou expire\"}";
            }

            Integer userId = activeTokens.get(token);
            User user = userRepository.findById(userId).orElse(null);

            if (user != null) {
                System.out.println("Utilisateur trouve: " + user.getEmail());

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

            System.out.println("Utilisateur non trouve en base");
            return "{\"error\": \"Utilisateur non trouve\"}";

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    //deconnecte user t supprimant son token de la liste des tokens
    @PostMapping("/logout")
    public String logout(@RequestBody Map<String, String> body) {
        try {
            String token = body.get("token");

            System.out.println("DECONNEXION");
            System.out.println("Token: " + token);

            if (token != null && activeTokens.containsKey(token)) {
                activeTokens.remove(token);
                System.out.println("Token supprime");
                System.out.println("Tokens restants: " + activeTokens.size());
            }

            return "{\"success\": true, \"message\": \"Déconnexion réussie\"}";

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    private String escapeJson(String value) {
        if (value == null)
            return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static Integer getUserIdFromToken(String token) {
        return activeTokens.get(token);
    }

    public static void removeToken(String token) {
        activeTokens.remove(token);
        System.out.println("Token supprime: " + token);
    }

    public static boolean isValidToken(String token) {
        return activeTokens.containsKey(token);
    }

    @GetMapping("/tokens/debug")
    public String debugTokens() {
        System.out.println("TOKENS ACTIFS");
        activeTokens.forEach((token, userId) -> {
            System.out.println("Token: " + token + " -> User ID: " + userId);
        });
        return "{\"activeTokens\": " + activeTokens.size() + "}";
    }

    //reinialisation du pswd
    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            String newPassword = body.get("newPassword");

            System.out.println("REINITIALISATION MOT DE PASSE");
            System.out.println("Email: " + email);

            User user = userRepository.findByEmail(email);
            if (user == null) {
                return "{\"error\": \"Email non trouve\"}";
            }

            user.setPassword(newPassword);
            userRepository.save(user);

            System.out.println("Mot de passe mis a jour pour: " + email);
            return "{\"success\": true, \"message\": \"Mot de passe mis à jour avec succès\"}";

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }
}