package com.example.demo.hooks;

import java.sql.Time;
import java.time.LocalDate;



public class AppelDTO {
     public int id;
    public LocalDate dateAppel;
    public Time heureDebut;
    public Time heureFin;
    public int duree;

    public AppelDTO(int id,LocalDate dateA,Time hd,Time hf,int duree){
        this.id=id;
        this.dateAppel=dateA;
        this.heureDebut=hd;
        this.duree=duree;
        this.heureFin=hf;
    }
}
