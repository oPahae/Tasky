package com.example.demo.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Projet")
public class Projet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nom;
    private String description;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private LocalDateTime deadline;
    private float budget;
    private float budgetConsomme;
    private String statut;
    private String code;

    @OneToMany(mappedBy = "projet")
    private List<Membre> membres;

    @OneToMany(mappedBy = "projet")
    private List<Tache> taches;

    @OneToMany(mappedBy = "projet")
    private List<Rapport> rapports;

    @OneToMany(mappedBy = "projet")
    private List<Message> messages;

    @OneToMany(mappedBy = "projet")
    private List<Appel> appels;

    public Projet(String nom, String description, LocalDateTime dateDebut, LocalDateTime deadline, float budget, String statut, String code) {
        this.nom = nom;
        this.description = description;
        this.dateDebut = LocalDateTime.now();
        this.deadline = deadline;
        this.budget = budget;
        this.budgetConsomme = 0;
        this.statut = statut;
        this.code = code;
    }
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }
    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }
    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    public float getBudget() { return budget; }
    public void setBudget(float budget) { this.budget = budget; }
    public float getBudgetConsomme() { return budgetConsomme; }
    public void setBudgetConsomme(float budgetConsomme) { this.budgetConsomme = budgetConsomme; }
    public String getStatut() { return statut; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public List<Membre> getMembres() { return membres; }
    public void setMembres(List<Membre> membres) { this.membres = membres; }
    public List<Tache> getTaches() { return taches; }
    public void setTaches(List<Tache> taches) { this.taches = taches; }
    public List<Rapport> getRapports() { return rapports; }
    public void setRapports(List<Rapport> rapports) { this.rapports = rapports; }
    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }
    public List<Appel> getAppels() { return appels; }
    public void setAppels(List<Appel> appels) { this.appels = appels; }
    public void setStatut(String statut) {
        this.statut = statut;
        if(statut.equals("terminé")) {
            this.dateFin=LocalDateTime.now();
        }
    }
}