package com.example.demo.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.hooks.MembreDTO;
import com.example.demo.hooks.TacheDTO;
import com.example.demo.models.Membre;
import com.example.demo.models.Projet;
import com.example.demo.models.SousTache;
import com.example.demo.models.Tache;
import com.example.demo.repositories.ProjetRepository;
import com.example.demo.repositories.TacheRepository;

@RestController
@RequestMapping("/api")
public class DashboardController {
    @Autowired
    private ProjetRepository projetRepository;
    @Autowired
    private TacheRepository tacheRepository;

    @GetMapping("dashboard/projet/{id}/Membrenbr")//nombre dyal mbre fprojet
    public int getNbrMembreProjet(@PathVariable int id) {
        Projet pr = projetRepository.findById(id);
        int nbr = pr.getMembres().size();
        return nbr;
    }

    @GetMapping("dashboard/projet/{id}/encours")//nombre dyal taches en cours fprojet
    public int getNbrProjetEnCours(@PathVariable int id) {
        Projet pr = projetRepository.findById(id);
        if (pr == null) return 0;
        List<Tache> taches = pr.getTaches();
        long nbr = taches.stream()
                .filter(t -> "En cours".equalsIgnoreCase(t.getEtat()))
                .count();

        return (int) nbr;

    }

    @GetMapping("dashboard/projet/{id}/terminer")//nombre dyal taches terminer fprojet
    public int getNbrProjetTerminer(@PathVariable int id) {
        Projet pr = projetRepository.findById(id);
        List<Tache> taches = pr.getTaches();
        long nbr = taches.stream()
                .filter(t -> "Terminée".equalsIgnoreCase(t.getEtat()))
                .count();

        return (int) nbr;

    }

    @GetMapping("dashboard/projet/{id}/nbrtache")//nombre dyal taches kamliin fprojet
    public int getNbrTacheProjet(@PathVariable int id) {
        Projet pr = projetRepository.findById(id);
        List<Tache> taches = pr.getTaches();
        return taches.size();
    }

    //kanhawlo tache jaya mn bdd l objet hitach man9druch nsiftu entite direct l front pour securite
    private TacheDTO convertToTacheDTO(Tache t) {
        if (t == null)
            return null;

        TacheDTO dto = new TacheDTO();
        dto.id = t.getId();
        dto.titre = (t.getTitre());
        dto.description = (t.getDescription());
        dto.etat = (t.getEtat());
        dto.dateCreation = (t.getDateCreation());
        dto.dateFin = (t.getDateFin());
        dto.dateLimite = (t.getDateLimite());
        dto.progres = (int) getProgress(t.getId());

        return dto;
    }
    
    @GetMapping("dashboard/projet/{id}/taches")//liste dyal taches bles info dyal tache fprojet
    public List<TacheDTO> getTachesProjet(@PathVariable int id) {
        Projet pr = projetRepository.findById(id);
        if (pr != null) {
            return pr.getTaches()
                    .stream()
                    .map(this::convertToTacheDTO)
                    .collect(Collectors.toList());
        }
        return null;
    }

    @GetMapping("dashboard/taches/{id}/progress")//% dyal wahed tache
    public double getProgress(@PathVariable int id) {
        Tache t = tacheRepository.findById(id).orElse(null);
        if (t != null) {
            List<SousTache> staches = t.getSousTaches();
            if (staches.size() == 0) {
                return 0;
            }
            double total = 0;
            total = staches.stream().filter(SousTache::isTermine)
                    .count();
            return (total / staches.size()) * 100;
        }
        return 0;
    }

    @GetMapping("dashboard/projet/{id}/Membre")//liste dyal membres fprojet
    public List<MembreDTO> getMembreProjet(@PathVariable int id) {
        Projet pr = projetRepository.findById(id);
        List<Membre> m = pr.getMembres();
        if (pr != null && m != null) {
            return m.stream().map(this::convertToMembreDTO).collect(Collectors.toList());
        }
        return null;

    }

    //kanhawlo membre jaya mn bdd l objet hitach man9druch nsiftu entite direct l front pour securite
    private MembreDTO convertToMembreDTO(Membre t) {
        if (t == null)
            return null;
        MembreDTO dto = new MembreDTO();
        dto.id = t.getId();
        dto.nom = (t.getNom());
        dto.email = (t.getEmail());
        dto.description = (t.getDescription());
        dto.role = (t.getRole());
        dto.type = (t.getType());

        return dto;
    }
}