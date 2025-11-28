package com.example.demo.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Commentaire")
public class Commentaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String contenu;
    private LocalDateTime dateCreation;

    @ManyToOne
    @JoinColumn(name = "tacheID")
    private Tache tache;

    @ManyToOne
    @JoinColumn(name = "membreID")
    private Membre membre;

    public Commentaire(String contenu, Tache tache, Membre membre) {
        this.contenu = contenu;
        this.dateCreation = LocalDateTime.now();
        this.tache = tache;
        this.membre = membre;
    }
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public Tache getTache() { return tache; }
    public void setTache(Tache tache) { this.tache = tache; }
    public Membre getMembre() { return membre; }
    public void setMembre(Membre membre) { this.membre = membre; }
}