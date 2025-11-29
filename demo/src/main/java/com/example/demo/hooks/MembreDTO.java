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
    public String password;
   public MembreDTO(){}
     public MembreDTO(String nom,String email,String description,String role,String type,String password,Date dateR) {
        this.nom=nom;
        this.email=email;
        this.description=description;
        this.role=role;
        this.type=type;
        this.password=password;
        this.dateRejointe=dateR;
    }
    
}
