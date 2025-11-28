package com.example.demo.models;

import jakarta.persistence.*;
import java.util.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "Appel")
public class Appel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDateTime dateAppel;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private int duree;

    @ManyToOne
    @JoinColumn(name = "projetID")
    private Projet projet;

    @ManyToOne
    @JoinColumn(name = "initiateurID")
    private Membre initiateur;

    public Appel( Projet projet, Membre initiateur) {
        this.dateAppel = LocalDateTime.now();
        this.heureDebut = LocalTime.now();
        this.projet = projet;
        this.initiateur = initiateur;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDateTime getDateAppel() { return dateAppel; }
    public void setDateAppel(LocalDateTime dateAppel) { this.dateAppel = dateAppel; }
    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }
    public LocalTime getHeureFin() { return heureFin; }
    public int getDuree() { return duree; }
    public void setDuree(int duree) { this.duree = duree; }
    public Projet getProjet() { return projet; }
    public void setProjet(Projet projet) { this.projet = projet; }
    public Membre getInitiateur() { return initiateur; }
    public void setInitiateur(Membre initiateur) { this.initiateur = initiateur; }
     public void setHeureFin(LocalTime heureFin) {
        this.heureFin = heureFin;
        this.duree = (int) java.time.Duration.between(this.heureDebut, this.heureFin).toMinutes();
    }
}