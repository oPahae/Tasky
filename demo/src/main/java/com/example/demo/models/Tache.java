package com.example.demo.models;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class Tache {
    
    private  int id;
    private  String titre;
    private  String description;
    private LocalDateTime dateCreation;
    private LocalDateTime dateFin;
    private LocalDateTime dateLimite;
    private List<SousTache> sousTaches;
    private String etat;
    private Projet projet;
    private Membre membre;
    private List <Commentaire> commentaires;
    private List <Blockage> blockage;
    private List<Document> documents;

 public Tache( String titre, String description, LocalDateTime dateLimite, List<SousTache> sousTaches, Projet projet, Membre membre) {
       
        this.titre = titre;
        this.description = description;
        this.dateLimite = dateLimite;
        this.sousTaches = sousTaches;
        this.etat = "en cours";
        this.projet = projet;
        this.membre = membre;
        this.dateFin=null;
        this.dateCreation=LocalDateTime.now();
    }

    public int getId() {
        return id; }

    public void setId(int id) {
        this.id = id;}

    public String getTitre() {
        return titre; }

    public void setTitre(String titre) {
        this.titre = titre;}

    public String getDescription() {
        return description;}

    public void setDescription(String description) {
        this.description = description;}

    public LocalDateTime getDateCreation() {
        return dateCreation;}

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation; }

    public LocalDateTime getDateFinale() {
        return dateFin; }

    public void setDateFinale(LocalDateTime dateFinale) {
        this.dateFin = dateFinale;}

    public List<SousTache> getSousTaches() {
        return sousTaches; }

    public void setSousTaches(List<SousTache> sousTaches) {
        this.sousTaches = sousTaches; }

    public String getEtat() {
        return etat; }

    public void setEtat(String etat) {
        this.etat = etat;
        if(etat.equals("terminée")) {
               this.dateFin=LocalDateTime.now();
            } }
    
    public Projet getProjet() {
        return projet;}

    public void setProjet(Projet projet) {
        this.projet = projet; }

    public Membre getMembre() {
        return membre; }

    public void setMembre(Membre membre) {
        this.membre = membre;
    }
    public List<Commentaire> getCommentaires() {
        return commentaires;
    }
    public void setCommentaires(List<Commentaire> commentaires) {
        this.commentaires = commentaires;
    }
    public List<Blockage> getBlockage() {
        return blockage;
    }
    public void setBlockage(List<Blockage> blockage) {
        this.blockage = blockage;
    }
    public List<Document> getDocuments() {
        return documents;
    }
    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
    public LocalDateTime getDateLimite() {
        return dateLimite ;
    }
    public void setDateLimite(LocalDateTime  dateLimite) {
        this.dateLimite = dateLimite;
        if(this.etat.equals("en cours") && dateLimite.isBefore(LocalDateTime.now())) {
            etat="Pas fini";
    }



}
}