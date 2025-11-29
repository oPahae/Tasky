package com.example.demo.models;

import jakarta.persistence.*;
import java.util.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "Appel")
public class Appel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDate dateAppel;
    private Time heureDebut;
    private Time heureFin;
    private int duree;

    @ManyToOne
    @JoinColumn(name = "projetID")
    private Projet projet;

    @ManyToOne
    @JoinColumn(name = "initiateurID")
    private Membre initiateur;

    public Appel(){}
    public Appel(Membre mbr,Projet pr){
        this.initiateur=mbr;
        this.projet=pr;
        this.dateAppel=LocalDate.now();
        this.duree=0;
    }
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDate getDateAppel() { return dateAppel; }
    public void setDateAppel(LocalDate dateAppel) { this.dateAppel = dateAppel; }
    public Time getHeureDebut() { return heureDebut; }
    public void setHeureDebut(Time heureDebut) { this.heureDebut = heureDebut; }
    public Time getHeureFin() { return heureFin; }
    public void setHeureFin(Time heureFin) { this.heureFin = heureFin; }
    public int getDuree() { return duree; }
    public void setDuree(int duree) { this.duree = duree; }
    public Projet getProjet() { return projet; }
    public void setProjet(Projet projet) { this.projet = projet; }
    public Membre getInitiateur() { return initiateur; }
    public void setInitiateur(Membre initiateur) { this.initiateur = initiateur; }
}