package com.example.demo.models;

import java.time.LocalDateTime;

public class User {
    private int id;
    private String nom;
    private String prenom;
    private String password;
    private String  competance;
    private int telephone;
    private boolean disponibilite;
    private String email;
    private LocalDateTime dateCreation;
    
    public User( String nom, String prenom, String password, String competance, int telephone, boolean disponibilite, String email) {
        this.nom = nom;
        this.prenom = prenom;
        this.password = password;
        this.competance = competance;
        this.telephone = telephone;
        this.disponibilite = disponibilite;
        this.email = email;
        this.dateCreation = LocalDateTime.now();
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public String getPrenom() {
        return prenom;
    }
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getCompetance() {
        return competance;
    }
    public void setCompetance(String competance) {
        this.competance = competance;
    }
    public int getTelephone() {
        return telephone;
    }
    public void setTelephone(int telephone) {
        this.telephone = telephone;
    }
    public boolean isDisponibilite() {
        return disponibilite;
    }
    public void setDisponibilite(boolean disponibilite) {
        this.disponibilite = disponibilite;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }


}
