package com.example.demo.hooks;

import java.util.Date;

public class ProjetDTO {
    public int id;
    public String nom;
    public String description;
    public Date dateDebut;
    public Date dateFin;
    public Date deadline;
    public float budget;
    public float budgetConsomme;
    public String statut;
    public String code;
    public int membreID;

    public ProjetDTO() {}

    public ProjetDTO(int id, String nom, String description, Date deadline, float budget, String code, Date dateDebut,
            float budgetc, String statut) {
        this.nom = nom;
        this.id = id;
        this.description = description;
        this.dateDebut = dateDebut;
        this.deadline = deadline;
        this.budget = budget;
        this.budgetConsomme = budgetc;
        this.statut = statut;
        this.code = code;
    }

}
