package com.example.demo.hooks;

import java.time.LocalDate;

public class UserDTO {
     public int id;
    public String nom;
    public String prenom;
    public String email;
    public String password;
    public String competance;
    public String telephone;
    public boolean disponibilite;
    public LocalDate dateCreation;
    public String verifCode;

     public UserDTO(int id,String nom, String prenom, String email, String password, String competance, String telephone, boolean disponibilite,LocalDate dateCreation ) {
        this.nom = nom;
        this.id=id;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.competance = competance;
        this.telephone = telephone;
        this.disponibilite = disponibilite;
        this.dateCreation = dateCreation;
       
    }

}
