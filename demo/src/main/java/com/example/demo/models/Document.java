package com.example.demo.models;
import java.time.LocalDateTime;
public class Document {
    private int id;
    private String nom;
    private String url;
    private Tache tache;
    private Projet projet;
    private String description;
    private LocalDateTime dateCreation;
 public Document(String nom, String url, Tache tache, String description, Projet projet) {
      
        this.nom = nom;
        this.url = url;
        this.tache = tache;
        this.description = description;
        this.dateCreation = LocalDateTime.now();
        this.projet = projet;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public Tache getTache() {
        return tache;
    }
    public void setTache(Tache tache) {
        this.tache = tache;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
    public Projet getProjet() {
        return projet;
    }
    public void setProjet(Projet projet) {
        this.projet = projet;
    }
    


}
