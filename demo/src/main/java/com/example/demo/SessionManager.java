package com.example.demo;

public class SessionManager {
    private static SessionManager instance;
    private String token;
    private String prenom;
    private String nom;
    private String email;
    private String competance;
    private String telephone;
    private Long userId;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public static void init() {
        getInstance();
    }

    // Méthode pour sauvegarder toutes les infos de session
    public void setUserSession(String token, String prenom, String nom, String email, 
                               String competance, String telephone, Long userId) {
        this.token = token;
        this.prenom = prenom;
        this.nom = nom;
        this.email = email;
        this.competance = competance;
        this.telephone = telephone;
        this.userId = userId;
    }

    // Méthode simplifiée pour infos de base
    public void setUserInfo(String prenom, String nom, String email) {
        this.prenom = prenom;
        this.nom = nom;
        this.email = email;
    }

    // Getters
    public String getToken() {
        return token;
    }

    public String getPrenom() {
        return prenom != null ? prenom : "";
    }

    public String getNom() {
        return nom != null ? nom : "";
    }

    public String getFullName() {
        return getPrenom() + " " + getNom();
    }

    public String getEmail() {
        return email != null ? email : "";
    }

    public String getCompetance() {
        return competance != null ? competance : "";
    }

    public String getTelephone() {
        return telephone != null ? telephone : "";
    }

    public Long getUserId() {
        return userId;
    }

    public boolean isLoggedIn() {
        return token != null && !token.isEmpty();
    }

    // Méthode pour se déconnecter
    public void logout() {
        this.token = null;
        this.prenom = null;
        this.nom = null;
        this.email = null;
        this.competance = null;
        this.telephone = null;
        this.userId = null;
    }

    // Méthode pour afficher les infos (debug)
    public void printSessionInfo() {
        System.out.println("=== SESSION INFO ===");
        System.out.println("Token: " + (token != null ? "***" + token.substring(Math.max(0, token.length() - 4)) : "null"));
        System.out.println("Nom complet: " + getFullName());
        System.out.println("Email: " + getEmail());
        System.out.println("Compétence: " + getCompetance());
        System.out.println("Téléphone: " + getTelephone());
        System.out.println("User ID: " + getUserId());
        System.out.println("===================");
    }
}