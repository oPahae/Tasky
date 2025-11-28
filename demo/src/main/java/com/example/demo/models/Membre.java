package com.example.demo.models;

import java.time.LocalDateTime;
import java.util.List;
public class Membre extends User {
    private LocalDateTime dateRejointe;
    private int id;
    private String role;
    private String type;
    private Projet projet;
    private List<Notification> notifications;
    private Appel appels;
    private List<Message> messages;
    private List<Tache> taches;
    private List<Commentaire> commentaires;

    
    public Membre(String nom , String prenom, String password, String competance, int telephone, boolean disponibilite, String email, String role, String type, Projet projet) {
        super( nom, prenom, password, competance, telephone, disponibilite, email);
        this.dateRejointe = LocalDateTime.now();
        this.role = role;
        this.type = type;
        this.projet = projet;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public LocalDateTime getDateRejointe() {
        return dateRejointe;
    }
    public void setDateRejointe(LocalDateTime dateRejointe) {
        this.dateRejointe = dateRejointe;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Projet getProjet() {
        return projet;
    }
    public void setProjet(Projet projet) {
        this.projet = projet;
    }
    public List<Notification> getNotifications() {
        return notifications;
    }
    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
    public Appel getAppels() {
        return appels;
    }
    public void setAppels(Appel appels) {
        this.appels = appels;
    }
    public List<Message> getMessages() {
        return messages;
    }
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
    public List<Tache> getTaches() {
        return taches;
    }
    public void setTaches(List<Tache> taches) {
        this.taches = taches;
    }
    public List<Commentaire> getCommentaires() {
        return commentaires;
    }
    public void setCommentaires(List<Commentaire> commentaires) {
        this.commentaires = commentaires;
    }


}
