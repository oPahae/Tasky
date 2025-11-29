package com.example.demo.hooks;

import java.util.Date;



public class MessageDTO {
     public int id;
     public String contenu;
     public Date dateEnvoi;
     public boolean estLu;

     public MessageDTO(int id,String contenu ,Date dateE,boolean estLu) {
        this.contenu = contenu;
        this.id=id;
        this.dateEnvoi = dateE;
        this.estLu = estLu;
    }

}
