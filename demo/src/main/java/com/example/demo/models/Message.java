package com.example.demo.models;

import java.time.LocalDateTime;

public class Message {
    private int id;
    private String contenu;
    private Membre expediteur;
    private LocalDateTime dateEnvoi;
    private boolean estLue;
    private Projet projet;
    public Message(String contenu, Membre expediteur, Projet projet) {
        this.contenu = contenu;
        this.expediteur = expediteur;
        this.projet = projet;
        this.dateEnvoi = LocalDateTime.now();
        this.estLue = false;
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
    public Membre getExpediteur() {
        return expediteur;
    }
    public void setExpediteur(Membre expediteur) {
        this.expediteur = expediteur;
    }
    public LocalDateTime getDateEnvoi() {
        return dateEnvoi;
    }
    public void setDateEnvoi(LocalDateTime dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }
    public boolean isEstLue() {
        return estLue;
    }
    public void setEstLue(boolean estLue) {
        this.estLue = estLue;
    }
    public Projet getProjet() {
        return projet;
    }
    public void setProjet(Projet projet) {
        this.projet = projet;
    }




}
