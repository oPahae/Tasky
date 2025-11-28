package com.example.demo.models;

import java.time.LocalDateTime;

public class Commentaire {
    private int id;
    private String contenu;
    private Membre auteur;
    private Tache tache;
    private LocalDateTime dateCreation;

    public Commentaire(String contenu, Membre auteur, Tache tache) {
        this.contenu = contenu;
        this.auteur = auteur;
        this.tache = tache;
        this.dateCreation = LocalDateTime.now();
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getContenu() {
        return contenu;
    }
    public void setContenu(String contenu) {
        this.contenu = contenu;
    }
    public Membre getAuteur() {
        return auteur;
    }
    public void setAuteur(Membre auteur) {
        this.auteur = auteur;
    }
    public Tache getTache() {
        return tache;
    }

    public void setTache(Tache tache) {
        this.tache = tache;
    }
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
    

}
