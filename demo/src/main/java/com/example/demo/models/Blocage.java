package com.example.demo.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Blocage")
public class Blocage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String description;
    private LocalDateTime dateSignalement;
    private String statut;
    private LocalDateTime dateResolution;

    @ManyToOne
    @JoinColumn(name = "tacheID")
    private Tache tache;
     public Blocage( String description, Tache tache) {
        this.description = description;
        this.dateSignalement = LocalDateTime.now();
        this.statut = "Ouvert";
        this.tache = tache;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getDateSignalement() { return dateSignalement; }
    public void setDateSignalement(LocalDateTime dateSignalement) { this.dateSignalement = dateSignalement; }
    public String getStatut() { return statut; }
     public void setStatut(String statut) {
        this.statut = statut;
        if(statut.equals("Fermer")) {
            this.dateResolution = LocalDateTime.now();
        }
    }
    public LocalDateTime getDateResolution() { return dateResolution; }
    public void setDateResolution(LocalDateTime dateResolution) { this.dateResolution = dateResolution; }
    public Tache getTache() { return tache; }
    public void setTache(Tache tache) { this.tache = tache; }
}