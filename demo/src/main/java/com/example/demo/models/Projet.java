package com.example.demo.models;

import java.time.LocalDateTime;
import java.util.List;
//tst
public class Projet {
    private int id;
    private String nom;
    private String description;
    private LocalDateTime dateDebut ;
    private LocalDateTime dateFin;
    private LocalDateTime Deadline;
    private float budget;
    private float budgetConsomme ;
    private String statut;
    private String code;
    private List<Membre> membres;
    private List<Tache> taches;
    private List<Document> documents;
    private List<Rapport> rapports;
    private List<Message> messages;
    private List<Appel> appels;


    public Projet(String name, String description, LocalDateTime dateFinale, float budget, List<Membre> membres, List<Tache> taches,LocalDateTime Deadline,String code) {
        this.nom = name;
        this.description = description;
        this.dateDebut  = LocalDateTime.now();
        this.dateFin = dateFinale;
        this.budget = budget;
        this.budgetConsomme  = 0;
        this.statut = "en cours";
        this.membres = membres;
        this.taches = taches;
        this.code=code;
        this.Deadline=Deadline;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return nom;
    }
    public void setName(String name) {
        this.nom = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public LocalDateTime getDateCreation() {
        return dateDebut ;
    }
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateDebut  = dateCreation;
    }
    public LocalDateTime getDateFinale() {
        return dateFin;
    }
    public void setDateFinale(LocalDateTime dateFinale) {
        this.dateFin = dateFinale;
    }
    public float getBudget() {
        return budget;
    }
    public void setBudget(float budget) {
        this.budget = budget;
    }
    public float getBudgetConsomme () {
        return   budgetConsomme ;
    }
    public void setBudgetConsomme (float depenses) {
        this.budgetConsomme  = depenses;
    }
    public String getStatut() {
        return statut;
    }
    public void setStatut(String statut) {
        this.statut = statut;
        if(statut.equals("terminé")) {
            this.dateFin=LocalDateTime.now();
        }
    }
    public List<Membre> getMembres() {
        return membres;
    }
    public void setMembres(List<Membre> membres) {
        this.membres = membres;
    }
    public List<Tache> getTaches() {
        return taches;
    }
    public void setTaches(List<Tache> taches) {
        this.taches = taches;
    }
    public List<Document> getDocuments() {
        return documents;
    }
    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
    public List<Message> getMessages() {
        return messages;
    }
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
    public List<Appel> getAppels() {
        return appels;
    }
    public void setAppels(List<Appel> appels) {
        this.appels = appels;
    }
    public List<Rapport> getRapports() {
        return rapports;
    }
    public void setRapports(List<Rapport> rapports) {
        this.rapports = rapports;
    }
    public LocalDateTime getDeadline() {
        return Deadline;
    }
    public void setDeadline(LocalDateTime deadline) {
        Deadline = deadline;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    

}
