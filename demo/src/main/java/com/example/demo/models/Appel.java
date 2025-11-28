package com.example.demo.models;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class Appel {
    private int id;
    private LocalDateTime dateAppel;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private int duree;
    private List<Membre> participants;
    private Projet projet;
    public Appel(List<Membre> participants, Projet projet) {
        this.dateAppel = LocalDateTime.now();
        this.heureDebut = LocalTime.now();
        this.participants = participants;
        this.projet = projet;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public LocalDateTime getDateAppel() {
        return dateAppel;
    }
    public void setDateAppel(LocalDateTime dateAppel) {
        this.dateAppel = dateAppel;
    }
    public LocalTime getHeureDebut() {
        return heureDebut;
    }
    public void setHeureDebut(LocalTime heureDebut) {
        this.heureDebut = heureDebut;
    }
    public LocalTime getHeureFin() {
        return heureFin;
    }
    public void setHeureFin(LocalTime heureFin) {
        this.heureFin = heureFin;
        this.duree = (int) java.time.Duration.between(this.heureDebut, this.heureFin).toMinutes();
    }
    public int getDuree() {
        return duree;
    }
    public List<Membre> getParticipants() {
        return participants;
    }
    public void setParticipants(List<Membre> participants) {
        this.participants = participants;
    }
    public Projet getProjet() {
        return projet;
    }

    public void setProjet(Projet projet) {
        this.projet = projet;
    }


}
