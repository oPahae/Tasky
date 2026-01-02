package com.example.demo.controllers;

import com.example.demo.hooks.AjouterTacheRequest;
import com.example.demo.hooks.MembreDTO;
import com.example.demo.models.Membre;
import com.example.demo.models.Projet;
import com.example.demo.models.Tache;
import com.example.demo.repositories.MembreRepository;
import com.example.demo.repositories.ProjetRepository;
import com.example.demo.repositories.TacheRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AjouterTacheController {

    private final TacheRepository tacheRepository;
    private final ProjetRepository projetRepository;
    private final MembreRepository membreRepository;

    public AjouterTacheController(
            TacheRepository tacheRepository,
            ProjetRepository projetRepository,
            MembreRepository membreRepository) {
        this.tacheRepository = tacheRepository;
        this.projetRepository = projetRepository;
        this.membreRepository = membreRepository;
    }

    @PostMapping("/ajouterTache")
    public ResponseEntity<?> ajouterTache(@RequestBody AjouterTacheRequest request) {
        try {
            if (request.getTitre() == null || request.getTitre().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Le titre de la tâche est obligatoire");
            }

            if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("La description de la tâche est obligatoire");
            }

            // prj kayn ?
            Projet projet = projetRepository.findById(request.getProjetId());
            if (projet == null) {
                return ResponseEntity.badRequest().body("Projet introuvable");
            }

            // membres dial lprj
            List<Membre> membresAssignes = new ArrayList<>();
            if (request.getMembreIds() != null && !request.getMembreIds().isEmpty()) {
                for (Integer membreId : request.getMembreIds()) {
                    Membre membre = membreRepository.findById(membreId).orElse(null);
                    if (membre != null && membre.getProjet().getId() == request.getProjetId()) {
                        membresAssignes.add(membre);
                    }
                }
            }

            // nparsiw date
            LocalDate dateLimite = null;
            if (request.getDateLimite() != null && !request.getDateLimite().trim().isEmpty()) {
                try {
                    String[] parts = request.getDateLimite().split("/");
                    if (parts.length == 3) {
                        int jour = Integer.parseInt(parts[0]);
                        int mois = Integer.parseInt(parts[1]);
                        int annee = Integer.parseInt(parts[2]);
                        dateLimite = LocalDate.of(annee, mois, jour);
                    }
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body("Format de date invalide. Utilisez JJ/MM/AAAA");
                }
            }

            // nkhshiw f table tache
            Tache nouvelleTache = new Tache();
            nouvelleTache.setTitre(request.getTitre());
            nouvelleTache.setDescription(request.getDescription());
            nouvelleTache.setDateLimite(dateLimite);
            nouvelleTache.setProjet(projet);
            nouvelleTache.setEtat("à faire");
            nouvelleTache.setDateCreation(LocalDate.now());
            Tache tacheSauvegardee = tacheRepository.save(nouvelleTache);

            // nkhshiw f table tachemembre
            if (!membresAssignes.isEmpty()) {
                tacheSauvegardee.setMembres(membresAssignes);
                for (Membre membre : membresAssignes) {
                    if (membre.getTaches() == null) {
                        membre.setTaches(new ArrayList<>());
                    }
                    membre.getTaches().add(tacheSauvegardee);
                }
                tacheRepository.save(tacheSauvegardee);
            }

            return ResponseEntity.ok().body("Tâche créée avec succès (ID: " + tacheSauvegardee.getId() + ")");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Erreur lors de la création de la tâche: " + e.getMessage());
        }
    }

    @GetMapping("/projets/{projetId}/membres")
    public ResponseEntity<?> getMembresByProjet(@PathVariable int projetId) {

        Projet projet = projetRepository.findById(projetId);
        if (projet == null) {
            return ResponseEntity.badRequest().body("Projet introuvable");
        }

        List<Membre> membres = membreRepository.findByProjetId(projetId);

        List<MembreDTO> membresDTO = membres.stream()
                .map(m -> {
                    MembreDTO dto = new MembreDTO();
                    dto.id = m.getId();
                    dto.nom = m.getNom();
                    dto.prenom = m.getPrenom();
                    dto.email = m.getEmail();
                    dto.description = m.getDescription();
                    dto.dateRejointe = m.getDateRejointe();
                    dto.role = m.getRole();
                    dto.type = m.getType();
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(membresDTO);
    }
}