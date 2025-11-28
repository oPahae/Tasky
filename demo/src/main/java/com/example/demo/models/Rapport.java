package com.example.demo.models;

import java.time.LocalDateTime;

public class Rapport {
    private int id;
    private LocalDateTime dateGeneration;
    private String nom;
    private Projet projet;
    
    public Rapport(String nom, Projet projet) {
        this.nom = nom;
        this.projet = projet;
        this.dateGeneration = LocalDateTime.now();
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public LocalDateTime getDateGeneration() {
        return dateGeneration;
    }
    public void setDateGeneration(LocalDateTime dateGeneration) {
        this.dateGeneration = dateGeneration;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public Projet getProjet() {
        return projet;
    }
    public void setProjet(Projet projet) {
        this.projet = projet;
    }
    

}
