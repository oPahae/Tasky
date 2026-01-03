package com.example.demo;

public class SessionManager {
    private static SessionManager instance;

    private String token;
    private int userId;
    private String prenom;
    private String nom;
    private String email;
    private String competance;
    private String telephone;

    private int currentProjetId;
    private String currentProjetNom;
    private String currentProjetDescription;

    private SessionManager() {
        this.currentProjetId = -1;
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setUserSession(String token, String prenom, String nom, String email,
                               String competance, String telephone, int userId) {
        this.token = token;
        this.prenom = prenom;
        this.nom = nom;
        this.email = email;
        this.competance = competance;
        this.telephone = telephone;
        this.userId = userId;
    }

    public void setCurrentProjet(int projetId, String projetNom, String projetDescription) {
        this.currentProjetId = projetId;
        this.currentProjetNom = projetNom;
        this.currentProjetDescription = projetDescription;
        System.out.println("Projet actuel defini : ID=" + projetId + ", Nom=" + projetNom);
    }

    public void setCurrentProjet(int projetId, String projetNom) {
        setCurrentProjet(projetId, projetNom, "");
    }

    public String getToken() {
        return token;
    }

    public int getUserId() {
        return userId;
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

    // GETTERS PROJET ACTUEL 
    public int getCurrentProjetId() {
        return currentProjetId;
    }

    public String getCurrentProjetNom() {
        return currentProjetNom != null ? currentProjetNom : "Projet";
    }

    public String getCurrentProjetDescription() {
        return currentProjetDescription != null ? currentProjetDescription : "";
    }

    public boolean isLoggedIn() {
        return token != null && !token.isEmpty() && userId > 0;
    }

    public boolean hasProjetSelected() {
        return currentProjetId > 0;
    }

    public boolean isReadyForChat() {
        return isLoggedIn() && hasProjetSelected();
    }

    public void logout() {
        this.token = null;
        this.prenom = null;
        this.nom = null;
        this.email = null;
        this.competance = null;
        this.telephone = null;
        this.userId = -1;
        this.currentProjetId = -1;
        this.currentProjetNom = null;
        this.currentProjetDescription = null;
    }

    // DEBUG
    public void printSessionInfo() {
        
        System.out.println("USER:");
        System.out.println("Token: " + (token != null ? "***" + token.substring(Math.max(0, token.length() - 4)) : "null"));
        System.out.println("User ID: " + userId);
        System.out.println("Nom complet: " + getFullName());
        System.out.println("Email: " + getEmail());
        System.out.println();
        System.out.println("PROJET ACTUEL:");
        System.out.println("Projet ID: " + currentProjetId);
        System.out.println("Nom: " + getCurrentProjetNom());
        System.out.println("Description: " + getCurrentProjetDescription());
        System.out.println();
        System.out.println("STATUS:");
        System.out.println("Logged in: " + isLoggedIn());
        System.out.println("Projet selected: " + hasProjetSelected());
        System.out.println("Ready for chat: " + isReadyForChat());
    }

   public static void init() {
    if (instance == null) {
        instance = new SessionManager();
    }
    
}
}
