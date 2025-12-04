package com.example.demo.models;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String contenu;
    private Date dateEnvoie;
    private boolean estLue;

    @ManyToOne
    @JoinColumn(name = "membreID")
    private Membre membre;

    @ManyToOne
    @JoinColumn(name = "projetID")
    private Projet projet;

    public Notification() {}
    public Notification(String contenu ,boolean estLue) {
        this.contenu = contenu;
        this.dateEnvoie = new Date();
        this.estLue = estLue;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public Date getDateEnvoie() { return dateEnvoie; }
    public void setDateEnvoie(Date dateEnvoie) { this.dateEnvoie = dateEnvoie; }
    public boolean isEstLue() { return estLue; }
    public void setEstLue(boolean estLue) { this.estLue = estLue; }
    public Membre getMembre() { return membre; }
    public void setMembre(Membre membre) { this.membre = membre; }
    public Projet getProjet() { return projet; }
    public void setProjet(Projet projet) { this.projet = projet; }
}