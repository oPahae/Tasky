package com.example.demo.models;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "Blocage")
public class Blocage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String description;
    private LocalDate dateSignalement;
    private String statut;
    private LocalDate dateResolution;

    @ManyToOne
    @JoinColumn(name = "tacheID")
    private Tache tache;

    public Blocage(){}
    public Blocage(String desc,String statut){
        this.description=desc;
        this.statut=statut;
        this.dateSignalement=LocalDate.now();
        this.dateResolution=null;
    }




    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getDateSignalement() { return dateSignalement; }
    public void setDateSignalement(LocalDate dateSignalement) { this.dateSignalement = dateSignalement; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; 
        if(statut.equals("Résolu")){
            this.dateResolution=LocalDate.now();
        }
    }
    public LocalDate getDateResolution() { return dateResolution; }
    public void setDateResolution(LocalDate dateResolution) { this.dateResolution = dateResolution; }
    public Tache getTache() { return tache; }
    public void setTache(Tache tache) { this.tache = tache; }
}