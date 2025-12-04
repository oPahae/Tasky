package com.example.demo.models;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "SousTache")
public class SousTache {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String titre;
    private LocalDate dateCreation;
    private LocalDate dateFin;
    private boolean termine;

    @ManyToOne
    @JoinColumn(name = "tacheID")
    private Tache tache;

    public SousTache() {}
    public SousTache(String titre ,LocalDate datefin){
        this.titre=titre;
        this.dateCreation=LocalDate.now();
        this.dateFin=datefin;;
        this.termine=false;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate  dateCreation) { this.dateCreation = dateCreation; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate  dateFin) { this.dateFin = dateFin; }
    public boolean isTermine() { return termine; }
    public void setTermine(boolean termine) { this.termine = termine; 
        if(termine){
            this.dateFin=LocalDate.now();
        }
    }
    public Tache getTache() { return tache; }
    public void setTache(Tache tache) { this.tache = tache; }
}