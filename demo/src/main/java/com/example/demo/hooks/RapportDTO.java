package com.example.demo.hooks;

import java.time.LocalDate;



public class RapportDTO {
     public int id;
    public LocalDate dateGeneration;
    public String nom;

    public RapportDTO(int id,String nom, LocalDate loc) {
        this.dateGeneration = loc;
        this.nom = nom;
        this.id = id;
    }
}
