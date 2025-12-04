package com.example.demo;

import java.io.*;
import java.nio.file.*;

public class TokenManager {
    
    // Chemin du fichier de stockage du token
    private static final String TOKEN_FILE = System.getProperty("user.home") + "/.tasky_token";
    
    // Sauvegarder le token dans un fichier
    public static void saveToken(String token) {
        try {
            Files.write(Paths.get(TOKEN_FILE), token.getBytes());
            System.out.println("Token sauvegardé dans: " + TOKEN_FILE);
        } catch (IOException e) {
            System.err.println("Erreur sauvegarde token: " + e.getMessage());
        }
    }
    
    // récupérer la session sauvegardée pour vérifier si l’utilisateur est déjà connecté
    public static String loadToken() {
        try {
            if (Files.exists(Paths.get(TOKEN_FILE))) {
                String token = new String(Files.readAllBytes(Paths.get(TOKEN_FILE)));
                System.out.println("Token chargé: " + token);
                return token;
            }
        } catch (IOException e) {
            System.err.println(" Erreur chargement token: " + e.getMessage());
        }
        return null;
    }
    
    // Supprimer le token
    public static void deleteToken() {
        try {
            Files.deleteIfExists(Paths.get(TOKEN_FILE));
            System.out.println(" Token supprimé");
        } catch (IOException e) {
            System.err.println("Erreur suppression token: " + e.getMessage());
        }
    }
    
    // Vérifier si un token existe
    public static boolean hasToken() {
        return Files.exists(Paths.get(TOKEN_FILE));
    }
}