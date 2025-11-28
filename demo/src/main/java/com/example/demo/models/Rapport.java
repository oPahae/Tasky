package com.example.demo.models;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Rapport")
public class Rapport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Date dateGeneration;
    private String nom;

    @ManyToOne
    @JoinColumn(name = "projetID")
    private Projet projet;

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Date getDateGeneration() { return dateGeneration; }
    public void setDateGeneration(Date dateGeneration) { this.dateGeneration = dateGeneration; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public Projet getProjet() { return projet; }
    public void setProjet(Projet projet) { this.projet = projet; }
}