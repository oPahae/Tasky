package com.example.demo.hooks;

import java.util.List;
import java.time.LocalDate;

public class TacheDTO {
    public int id;
    public String titre;
    public String description;
    public LocalDate dateLimite;
    public String etat;
    public LocalDate dateCreation;
    public LocalDate dateFin;
    public int progres;
    public List<SousTacheDTO> sousTaches;
    public List<Integer> membreIds;

    public TacheDTO() {}

    public TacheDTO(int id, String titre, String description, LocalDate dateLimite, String etat,
            LocalDate dateCreation, LocalDate dateFin, int progres) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.dateLimite = dateLimite;
        this.etat = etat;
        this.dateCreation = dateCreation;
        this.dateFin = dateFin;
        this.progres = progres;
    }

    public TacheDTO(int id, String titre, String description, LocalDate dateLimite, String etat,
            LocalDate dateCreation, LocalDate dateFin, int progres, List<SousTacheDTO> sousTaches) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.dateLimite = dateLimite;
        this.etat = etat;
        this.dateCreation = dateCreation;
        this.dateFin = dateFin;
        this.progres = progres;
        this.sousTaches = sousTaches;
    }
}
