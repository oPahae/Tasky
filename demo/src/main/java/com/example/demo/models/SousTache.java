package com.example.demo.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "SousTache")
public class SousTache {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String titre;
    private LocalDateTime dateCreation;
    private LocalDateTime dateFin;
    private boolean termine;

    @ManyToOne
    @JoinColumn(name = "tacheID")
    private Tache tache;


    public SousTache() {
    }
    public SousTache(String titre, LocalDate dateCreation, boolean termine, Tache tache) {
        this.titre = titre;
        this.dateCreation = LocalDateTime.now();
        this.termine = false;
        this.tache = tache;
    }
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime  dateCreation) { this.dateCreation = dateCreation; }
    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime  dateFin) { this.dateFin = dateFin; }
    public boolean isTermine() { return termine; }
    public void setTermine(boolean etat) {
        this.termine = etat;
        if(etat) {
               this.dateFin=LocalDateTime.now();
    }}
    public Tache getTache() { return tache; }
    public void setTache(Tache tache) { this.tache = tache; }
}