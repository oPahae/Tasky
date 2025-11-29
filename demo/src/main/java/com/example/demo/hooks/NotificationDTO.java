package com.example.demo.hooks;

import java.util.Date;

public class NotificationDTO {
    public int id;
    public String contenu;
    public Date dateEnvoie;
    public boolean estLue;

    public NotificationDTO(int id,String contenu ,Date dateE,boolean estLue) {
        this.contenu = contenu;
        this.dateEnvoie = dateE;
        this.estLue = estLue;
        this.id=id;
    }

}
