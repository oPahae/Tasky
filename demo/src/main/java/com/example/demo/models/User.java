package com.example.demo.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String competance;
    private String telephone;
    
    @Column(columnDefinition = "boolean default true")
    private boolean disponibilite = true; // Par défaut true
    
    private LocalDate dateCreation;
    private String verifCode;

    @OneToMany(mappedBy = "user")
    private List<Membre> membres;

    public User(){}
    
    public User(String nom, String prenom, String email, String password, String competance, String telephone) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.competance = competance;
        this.telephone = telephone;
        this.disponibilite = true; // Par défaut true
        this.dateCreation = LocalDate.now(); // Date système
    }
    
    // Méthode appelée avant l'insertion en base de données
    @PrePersist
    protected void onCreate() {
        if (this.dateCreation == null) {
            this.dateCreation = LocalDate.now();
        }
        // S'assurer que disponibilite est true par défaut
        if (!this.disponibilite) {
            this.disponibilite = true;
        }
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getCompetance() { return competance; }
    public void setCompetance(String competance) { this.competance = competance; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public boolean isDisponibilite() { return disponibilite; }
    public void setDisponibilite(boolean disponibilite) { this.disponibilite = disponibilite; }
    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }
    public String getVerifCode() { return verifCode; }
    public void setVerifCode(String verifCode) { this.verifCode = verifCode; }
    public List<Membre> getMembres() { return membres; }
    public void setMembres(List<Membre> membres) { this.membres = membres; }
}
