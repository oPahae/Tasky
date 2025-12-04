package com.example.demo.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Tache")
public class Tache {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String titre;
    private String description;
    private LocalDate dateLimite;
    private String etat;
    private LocalDate dateCreation;
    private LocalDate dateFin;
    private int depense;

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

    public Tache() {}
    public Tache(String titre,String description,LocalDate dateLimite,Projet projet,List<Membre> membres) {
        this.titre=titre;
        this.description=description;
        this.dateLimite=dateLimite;
        this.projet=projet;
        this.membres=membres;
        this.dateCreation=LocalDate.now();

    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getDateLimite() { return dateLimite; }
    public void setDateLimite(LocalDate dateLimite) { this.dateLimite = dateLimite; }
    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; 
    if(etat.equals("terminée")){
        this.dateFin=LocalDate.now();
    }}
    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public int getDepense() { return depense; }
    public void setDepense(int depense) { this.depense = depense; }
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