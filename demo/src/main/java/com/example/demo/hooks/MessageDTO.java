package com.example.demo.hooks;

import java.util.Date;



public class MessageDTO {
     public int id;
     public String contenu;
     public Date dateEnvoi;
     public boolean estLu;
     public int projetId; //zedt projetId = l’ID du projet auquel le message appartient
     public int membreId; //zedt membreId = l’ID de l’utilisateur qui envoie le message


     public MessageDTO(int id,String contenu ,Date dateE,boolean estLu,int projetId, int membreId) {
        this.contenu = contenu;
        this.id=id;
        this.dateEnvoi = dateE;
        this.estLu = estLu;
        this.projetId = projetId;  //zedt
        this.membreId = membreId;  //zedt
    }
     public MessageDTO() {} // darori pour Spring
}
