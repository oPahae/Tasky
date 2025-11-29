package com.example.demo.hooks;

import java.time.LocalDate;

public class DocumentDTO {
    public int id;
    public String nom;
    public String description;
    public String contenuBase64;
    public LocalDate dateCreation;
    public int size;
    public String type;

    public DocumentDTO() {
    }

    public DocumentDTO(int id, String nom, String description, String contenuBase64, LocalDate dateCreation, int size,
            String type) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.contenuBase64 = contenuBase64;
        this.dateCreation = dateCreation;
        this.size = size;
        this.type = type;
    }

    public static String getFileType(String fileName) {
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        switch (ext) {
            case "pdf":
                return "application/pdf";
            case "png":
            case "jpg":
            case "jpeg":
                return "image/" + ext;
            case "doc":
            case "docx":
                return "application/msword";
            default:
                return "application/octet-stream";
        }
    }
}