package com.example.demo.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Document")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nom;
    private String description;
    private LocalDateTime dateCreation;
    private byte[] contenu;

    @ManyToOne
    @JoinColumn(name = "tacheID")
    private Tache tache;

    public Document( String nom, String description, byte[] contenu, Tache tache) {
        this.nom = nom;
        this.description = description;
        this.dateCreation = LocalDateTime.now();
        this.contenu = contenu;
        this.tache = tache;
    }
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public byte[] getContenu() { return contenu; }
    public void setContenu(byte[] contenu) { this.contenu = contenu; }
    public Tache getTache() { return tache; }
    public void setTache(Tache tache) { this.tache = tache; }
}