package com.example.demo.hooks;

import java.util.List;

public class AjouterTacheRequest {
    private String titre;
    private String description;
    private String dateLimite; // format yyyy-MM-dd
    private int projetId;
    private List<Integer> membreIds;

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDateLimite() { return dateLimite; }
    public void setDateLimite(String dateLimite) { this.dateLimite = dateLimite; }

    public int getProjetId() { return projetId; }
    public void setProjetId(int projetId) { this.projetId = projetId; }

    public List<Integer> getMembreIds() { return membreIds; }
    public void setMembreIds(List<Integer> membreIds) { this.membreIds = membreIds; }
}