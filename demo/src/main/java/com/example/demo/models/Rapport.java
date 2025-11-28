package com.example.demo.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "Rapport")
public class Rapport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalDateTime dateGeneration;
    private String nom;

    @ManyToOne
    @JoinColumn(name = "projetID")
    private Projet projet;

    public Rapport( String nom, Projet projet) {
        this.dateGeneration = LocalDateTime.now();
        this.nom = nom;
        this.projet = projet;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDateTime getDateGeneration() { return dateGeneration; }
    public void setDateGeneration(LocalDateTime dateGeneration) { this.dateGeneration = dateGeneration; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public Projet getProjet() { return projet; }
    public void setProjet(Projet projet) { this.projet = projet; }
}