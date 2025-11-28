package com.example.demo.models;

import jakarta.persistence.*;

@Embeddable
public class TacheMembre {
    @ManyToOne
    @JoinColumn(name = "tacheID")
    private Tache tache;

    @ManyToOne
    @JoinColumn(name = "membreID")
    private Membre membre;

    // Getters et Setters
    public Tache getTache() { return tache; }
    public void setTache(Tache tache) { this.tache = tache; }
    public Membre getMembre() { return membre; }
    public void setMembre(Membre membre) { this.membre = membre; }
}