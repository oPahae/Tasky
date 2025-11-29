package com.example.demo.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Document")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nom;
    private String description;
    private LocalDate dateCreation;

    @Lob
    private byte[] contenu;

    @ManyToOne
    @JoinColumn(name = "tacheID")
    private Tache tache;


    public Document(){}
    public Document(String nom,String description,byte[] contenu){
        this.contenu=contenu;
        this.nom=nom;
        this.description=description;
        this.dateCreation=LocalDate.now();
    }
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }
    public byte[] getContenu() { return contenu; }
    public void setContenu(byte[] contenu) { this.contenu = contenu; }
    public Tache getTache() { return tache; }
    public void setTache(Tache tache) { this.tache = tache; }
}