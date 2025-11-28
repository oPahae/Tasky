package com.example.demo.models;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
import java.time.LocalDateTime;

@Entity
@Table(name = "Membre")
public class Membre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String description;
    private LocalDateTime dateRejointe;
    private String role;
    private String type;
    private String email;

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

    public Membre(String description, String role, String type, User user, Projet projet,String email) {
        this.description = description;
        this.dateRejointe = LocalDateTime.now();
        this.role = role;
        this.email=email;
        this.type = type;
        this.user = user;
        this.projet = projet;
    }
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getDateRejointe() { return dateRejointe; }
    public void setDateRejointe(LocalDateTime dateRejointe) { this.dateRejointe = dateRejointe; }
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
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}