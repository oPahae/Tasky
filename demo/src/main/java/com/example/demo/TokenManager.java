package com.example.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TokenManager {
    
    private static final String TOKEN_FILE = System.getProperty("user.home") + "/.tasky_token";
    
    public static void saveToken(String token) {
        try {
            Files.write(Paths.get(TOKEN_FILE), token.getBytes());
            System.out.println("Token sauvegarde dans: " + TOKEN_FILE);
        } catch (IOException e) {
            System.err.println("Erreur sauvegarde token: " + e.getMessage());
        }
    }
    
    public static String loadToken() {
        try {
            if (Files.exists(Paths.get(TOKEN_FILE))) {
                String token = new String(Files.readAllBytes(Paths.get(TOKEN_FILE)));
                System.out.println("Token charge: " + token);
                return token;
            }
        } catch (IOException e) {
            System.err.println("Erreur chargement token: " + e.getMessage());
        }
        return null;
    }
    
    public static void deleteToken() {
        try {
            Files.deleteIfExists(Paths.get(TOKEN_FILE));
            System.out.println("Token supprimes");
        } catch (IOException e) {
            System.err.println("Erreur suppression token: " + e.getMessage());
        }
    }
    
    public static boolean hasToken() {
        return Files.exists(Paths.get(TOKEN_FILE));
    }
}