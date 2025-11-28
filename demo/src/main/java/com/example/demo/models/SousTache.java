package com.example.demo.models;

import java.time.LocalDateTime;

public class SousTache {
    private  int id;
    private  String titre;
    private  String description;
    private LocalDateTime dateCreation;
    private LocalDateTime dateFin;
    private boolean termine;
    private Tache tache;

    public SousTache( String titre, String description, LocalDateTime dateFinale, Tache tache) {
        this.titre = titre;
        this.description = description;
        this.dateFin= dateFinale;
        this.termine = false;
        this.tache=tache;
         dateCreation=LocalDateTime.now();
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitre() {
        return titre;
    }
    public void setTitre(String titre) {
        this.titre = titre;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
    public LocalDateTime getDateFinale() {
        return dateFin;
    }
    public void setDateFinale(LocalDateTime dateFinale) {
        this.dateFin= dateFinale;
    }
    public boolean getEtat() {
        return termine;
    }
    public void setEtat(boolean etat) {
        this.termine = etat;
        if(etat) {
               this.dateFin=LocalDateTime.now();
    }}
    public Tache getTache() {
        return tache;
    }
    public void setTache(Tache tache) {
        this.tache = tache;
    }


}