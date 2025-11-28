package com.example.demo.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "Message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String contenu;
    private LocalDateTime dateEnvoi;
    private boolean estLu;

    @ManyToOne
    @JoinColumn(name = "membreID")
    private Membre membre;

    @ManyToOne
    @JoinColumn(name = "projetID")
    private Projet projet;

    public Message(String contenu, Membre membre, Projet projet) {
        this.contenu = contenu;
        this.dateEnvoi = LocalDateTime.now();
        this.estLu = false;
        this.membre = membre;
        this.projet = projet;
    }
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public LocalDateTime getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(LocalDateTime dateEnvoi) { this.dateEnvoi = dateEnvoi; }
    public boolean isEstLu() { return estLu; }
    public void setEstLu(boolean estLu) { this.estLu = estLu; }
    public Membre getMembre() { return membre; }
    public void setMembre(Membre membre) { this.membre = membre; }
    public Projet getProjet() { return projet; }
    public void setProjet(Projet projet) { this.projet = projet; }
}