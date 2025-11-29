package com.example.demo.hooks;

import java.time.LocalDate;

public class BlocageDTO {
    public int id;
    public String description;
    public LocalDate dateSignalement;
    public String statut;
    public LocalDate dateResolution;
    public BlocageDTO(){}

    public BlocageDTO(int id,String desc,LocalDate dateS,String statut,LocalDate dateR){
        this.id=id;
        this.description=desc;
        this.statut=statut;
        this.dateSignalement=dateS;
        this.dateResolution=dateR;
    }
    
}
