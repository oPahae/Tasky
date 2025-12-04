package com.example.demo.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "Rapport")
public class Rapport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDate dateGeneration;
    private String nom;

    @ManyToOne
    @JoinColumn(name = "projetID")
    private Projet projet;

    public Rapport() {}
    public Rapport(String nom, Projet projet) {
        this.dateGeneration = LocalDate.now();
        this.nom = nom;
        this.projet = projet;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDate getDateGeneration() { return dateGeneration; }
    public void setDateGeneration(LocalDate dateGeneration) { this.dateGeneration = dateGeneration; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public Projet getProjet() { return projet; }
    public void setProjet(Projet projet) { this.projet = projet; }
}