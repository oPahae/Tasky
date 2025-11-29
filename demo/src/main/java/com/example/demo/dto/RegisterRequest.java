package com.example.demo.dto;


    public record RegisterRequest(
        String nom,
        String prenom,
        String email,
        String password,
        String confirmPassword,
        String competance,
        String telephone
       // Boolean disponibilite
) {}

/* record katsawb automatiquement les getters, constructors u toString u kikuno fiha que les donnes li 3titiha 
 RegisterRequest katsifet les donnes li 3titiha f form dyal registration l backend
 */