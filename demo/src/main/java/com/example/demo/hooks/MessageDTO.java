package com.example.demo.hooks;

import java.util.Date;

public class MessageDTO {
    public int id;
    public String contenu;
    public Date dateEnvoi;
    public boolean estLu;
    public int projetId;
    public int membreId;
    public String prenomMembre;
    public String nomMembre;
    public String typeMembre;

    public MessageDTO() {}

    public MessageDTO(int id, String contenu, Date dateE, boolean estLu, 
                      int projetId, int membreId) {
        this.id = id;
        this.contenu = contenu;
        this.dateEnvoi = dateE;
        this.estLu = estLu;
        this.projetId = projetId;
        this.membreId = membreId;
    }

    public MessageDTO(int id, String contenu, Date dateE, boolean estLu, 
                      int projetId, int membreId, String prenomMembre, 
                      String nomMembre, String typeMembre) {
        this.id = id;
        this.contenu = contenu;
        this.dateEnvoi = dateE;
        this.estLu = estLu;
        this.projetId = projetId;
        this.membreId = membreId;
        this.prenomMembre = prenomMembre;
        this.nomMembre = nomMembre;
        this.typeMembre = typeMembre;
    }
}