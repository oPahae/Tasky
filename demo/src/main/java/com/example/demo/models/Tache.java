package com.example.demo.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Tache")
public class Tache {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String titre;
    private String description;
    private LocalDateTime dateLimite;
    private String etat;
    private LocalDateTime dateCreation;
    private LocalDateTime dateFin;

    @ManyToOne
    @JoinColumn(name = "projetID")
    private Projet projet;

    @OneToMany(mappedBy = "tache")
    private List<SousTache> sousTaches;

    @OneToMany(mappedBy = "tache")
    private List<Commentaire> commentaires;

    @OneToMany(mappedBy = "tache")
    private List<Document> documents;

    @OneToMany(mappedBy = "tache")
    private List<Blocage> blocages;

    @ManyToMany(mappedBy = "taches")
    private List<Membre> membres;

    public Tache() {
    }
    public Tache(String titre, String description, LocalDateTime dateLimite, String etat, Projet projet) {
        this.titre = titre;
        this.description = description;
        this.dateLimite = dateLimite;
        this.etat = "en cours";
        this.dateCreation = LocalDateTime.now();
        this.dateFin = null;
        this.projet = projet;
    }
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getDateLimite() { return dateLimite; }
     public void setDateLimite(LocalDateTime  dateLimite) {
        this.dateLimite = dateLimite;
        if(this.etat.equals("en cours") && dateLimite.isBefore(LocalDateTime.now())) {
            etat="Pas fini";
    }}
    public String getEtat() { return etat; }
    public void setEtat(String etat) {
        this.etat = etat;
        if(etat.equals("terminée")) {
               this.dateFin=LocalDateTime.now();
            } }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }
    public Projet getProjet() { return projet; }
    public void setProjet(Projet projet) { this.projet = projet; }
    public List<SousTache> getSousTaches() { return sousTaches; }
    public void setSousTaches(List<SousTache> sousTaches) { this.sousTaches = sousTaches; }
    public List<Commentaire> getCommentaires() { return commentaires; }
    public void setCommentaires(List<Commentaire> commentaires) { this.commentaires = commentaires; }
    public List<Document> getDocuments() { return documents; }
    public void setDocuments(List<Document> documents) { this.documents = documents; }
    public List<Blocage> getBlocages() { return blocages; }
    public void setBlocages(List<Blocage> blocages) { this.blocages = blocages; }
    public List<Membre> getMembres() { return membres; }
    public void setMembres(List<Membre> membres) { this.membres = membres; }
}