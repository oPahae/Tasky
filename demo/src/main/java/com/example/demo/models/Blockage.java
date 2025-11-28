package com.example.demo.models;

import java.time.LocalDateTime;

public class Blockage {
    private int id;
    private Tache tache;
    private String description;
    private LocalDateTime DateSignalement;
    private boolean statut;
    private LocalDateTime dateResolution;
    
    public Blockage(Tache tache, String description) {
        this.tache = tache;
        this.description = description;
        this.DateSignalement = LocalDateTime.now();
        this.statut = false;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Tache getTache() {
        return tache;
    }
    public void setTache(Tache tache) {
        this.tache = tache;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public LocalDateTime getDateSignalement() {
        return DateSignalement;
    }
    public void setDateSignalement(LocalDateTime dateSignalement) {
        DateSignalement = dateSignalement;
    }
    public boolean isStatut() {
        return statut;
    }
    public void setStatut(boolean statut) {
        this.statut = statut;
        if(statut) {
            this.dateResolution = LocalDateTime.now();
        }
    }
    public LocalDateTime getDateResolution() {
        return dateResolution;
    }
    public void setDateResolution(LocalDateTime dateResolution) {
        this.dateResolution = dateResolution;
    }

}
