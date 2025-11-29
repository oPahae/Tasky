package com.example.demo.hooks;

import java.time.LocalDate;

public class SousTacheDTO {
    public int id;
    public String titre;
    public boolean termine;
     public LocalDate dateCreation;
    public LocalDate dateFin;
public SousTacheDTO(){}
    public SousTacheDTO(int id, String titre, boolean termine,LocalDate datc,LocalDate datf) {
        this.id = id;
        this.dateCreation=datc;
        this.dateFin=datf;
        this.titre = titre;
        this.termine = termine;
    }
}