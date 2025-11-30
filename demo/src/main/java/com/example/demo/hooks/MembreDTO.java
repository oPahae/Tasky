package com.example.demo.hooks;

import java.util.Date;

public class MembreDTO {
    public int id;
    public String nom;
    public String email;
    public String description;
    public Date dateRejointe;
    public String role;
    public String type;
   public MembreDTO(){}
     public MembreDTO(String nom,String email,String description,String role,String type,Date dateR) {
        this.nom=nom;
        this.email=email;
        this.description=description;
        this.role=role;
        this.type=type;
        this.dateRejointe=dateR;
    }
    
}
