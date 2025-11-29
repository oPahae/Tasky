package com.example.demo.hooks;
import java.time.LocalDate;

public class CommentaireDTO {
    public int id;
    public String author;
    public String contenu;
    public LocalDate dateCreation;

    public CommentaireDTO(int id, String author, String contenu, LocalDate dateCreation) {
        this.id = id;
        this.author = author;
        this.contenu = contenu;
        this.dateCreation = dateCreation;
    }
}