package com.example.demo.models;

import java.time.LocalDateTime;

public class Notification {
    
 private int id;
 private String contenu;
 private LocalDateTime dateEnvoie;
 private Membre destinataire;
 private boolean estLue;

    public Notification(String contenu, Membre destinataire) {
        
        this.contenu = contenu;
        this.destinataire = destinataire;
        this.dateEnvoie = LocalDateTime.now();
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
    public LocalDateTime getDateEnvoie() {
        return dateEnvoie;
    }
    public void setDateEnvoie(LocalDateTime dateEnvoie) {
        this.dateEnvoie = dateEnvoie;
    }
    public Membre getDestinataire() {
        return destinataire;
    }
    public void setDestinataire(Membre destinataire) {
        this.destinataire = destinataire;
    }
    public boolean isEstLue() {
        return estLue;
    }
    public void setEstLue(boolean estLue) {
        this.estLue = estLue;
    }


}
