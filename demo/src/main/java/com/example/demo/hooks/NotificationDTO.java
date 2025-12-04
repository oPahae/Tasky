package com.example.demo.hooks;

import java.util.Date;

public class NotificationDTO {
    public int id;
    public String contenu;
    public Date dateEnvoie;
    public boolean estLue;

    public NotificationDTO(int id, String contenu, Date dateEnvoie, boolean estLue) {
        this.id = id;
        this.contenu = contenu;
        this.dateEnvoie = dateEnvoie;
        this.estLue = estLue;
    }
}