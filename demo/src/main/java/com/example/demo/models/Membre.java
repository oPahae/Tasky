package com.example.demo.models;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Membre")
public class Membre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String description;
    private Date dateRejointe;
    private String role;
    private String type;

    @ManyToOne
    @JoinColumn(name = "userID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "projetID")
    private Projet projet;

    @OneToMany(mappedBy = "membre")
    private List<Commentaire> commentaires;

    @OneToMany(mappedBy = "membre")
    private List<Message> messages;

    @OneToMany(mappedBy = "membre")
    private List<Notification> notifications;

    @ManyToMany
    @JoinTable(
        name = "TacheMembre",
        joinColumns = @JoinColumn(name = "membreID"),
        inverseJoinColumns = @JoinColumn(name = "tacheID")
    )
    private List<Tache> taches;

    public Membre(){}
    public Membre(String nom,String email,String description,String role,String type) {
        this.nom=nom;
        this.email=email;
        this.description=description;
        this.role=role;
        this.type=type;
        this.dateRejointe=new Date();
    }


    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getDateRejointe() { return dateRejointe; }
    public void setDateRejointe(Date dateRejointe) { this.dateRejointe = dateRejointe; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Projet getProjet() { return projet; }
    public void setProjet(Projet projet) { this.projet = projet; }
    public List<Commentaire> getCommentaires() { return commentaires; }
    public void setCommentaires(List<Commentaire> commentaires) { this.commentaires = commentaires; }
    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }
    public List<Notification> getNotifications() { return notifications; }
    public void setNotifications(List<Notification> notifications) { this.notifications = notifications; }
    public List<Tache> getTaches() { return taches; }
    public void setTaches(List<Tache> taches) { this.taches = taches; }
    public String getNom() { return nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getPrenom() { return prenom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}