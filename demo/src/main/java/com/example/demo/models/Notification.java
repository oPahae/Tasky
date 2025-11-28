package com.example.demo.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "Notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String contenu;
    private LocalDateTime dateEnvoie;
    private boolean estLue;

    @ManyToOne
    @JoinColumn(name = "membreID")
    private Membre membre;

    public Notification(String contenu, Membre membre) {
        this.contenu = contenu;
        this.dateEnvoie = LocalDateTime.now();
        this.estLue = false;
        this.membre = membre;
    }
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public LocalDateTime getDateEnvoie() { return dateEnvoie; }
    public void setDateEnvoie(LocalDateTime dateEnvoie) { this.dateEnvoie = dateEnvoie; }
    public boolean isEstLue() { return estLue; }
    public void setEstLue(boolean estLue) { this.estLue = estLue; }
    public Membre getMembre() { return membre; }
    public void setMembre(Membre membre) { this.membre = membre; }
}