package com.example.demo.hooks;

import java.util.Date;

public class MessageDTO {
    private int id;
    private String contenu;
    private Date dateEnvoi;
    private boolean estLu;
    private Integer projetId;  //zedt projetId = l’ID du projet auquel le msg appartient
    private Integer membreId;  //zedt membreId = l’ID de l’utilisateur qui envoie le msg 

    // Constructeur vide (requis pour Jackson)
    public MessageDTO() {}

    // Constructeur complet
    public MessageDTO(int id, String contenu, Date dateEnvoi, boolean estLu, Integer projetId, Integer membreId) {
        this.id = id;
        this.contenu = contenu;
        this.dateEnvoi = dateEnvoi;
        this.estLu = estLu;
        this.projetId = projetId;  //zedt
        this.membreId = membreId;   //zedt
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Date getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(Date dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }

    public boolean isEstLu() {
        return estLu;
    }

    public void setEstLu(boolean estLu) {
        this.estLu = estLu;
    }

    public Integer getProjetId() {
        return projetId;
    }

    public void setProjetId(Integer projetId) {
        this.projetId = projetId;
    }

    public Integer getMembreId() {
        return membreId;
    }

    public void setMembreId(Integer membreId) {
        this.membreId = membreId;
    }
}