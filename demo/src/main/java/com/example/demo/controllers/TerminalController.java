package com.example.demo.controllers;

import com.example.demo.hooks.*;
import com.example.demo.models.*;
import com.example.demo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/terminal")
public class TerminalController {

    @Autowired
    private TacheRepository tacheRepository;

    @Autowired
    private SousTacheRepository sousTacheRepository;

    @Autowired
    private ProjetRepository projetRepository;

    @Autowired
    private MembreRepository membreRepository;

    @Autowired
    private UserRepository userRepository;

    // ==================== Gestion des projets ====================

    @GetMapping("/projets/{id}")
    public ResponseEntity<Map<String, Object>> getProject(@PathVariable int id) {
        Projet projet = projetRepository.findById(id);
        if (projet == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Projet non trouvé"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", projet.getId());
        response.put("nom", projet.getNom());
        response.put("description", projet.getDescription());
        response.put("dateDebut", projet.getDateDebut());
        response.put("dateFin", projet.getDateFin());
        response.put("statut", projet.getStatut());
        response.put("budget", projet.getBudget());
        response.put("budgetConsomme", projet.getBudgetConsomme());

        return ResponseEntity.ok(Map.of("data", response));
    }

    @GetMapping("/projets/{id}/details")
    public ResponseEntity<Map<String, Object>> getProjectDetails(@PathVariable int id) {
        return getProject(id);
    }

    // ==================== Gestion des tâches ====================

    @GetMapping("/projets/{projetId}/taches")
    public ResponseEntity<Map<String, Object>> getTasksByProject(
            @PathVariable int projetId,
            @RequestParam(required = false) String etat) {

        Projet projet = projetRepository.findById(projetId);
        if (projet == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Projet non trouvé"));
        }

        List<Tache> taches = tacheRepository.findByProjetId(projetId);
        if (etat != null && etat.equals("Terminé")) {
            taches = taches.stream()
                    .filter(t -> "Terminé".equals(t.getEtat()))
                    .collect(Collectors.toList());
        }

        List<Map<String, Object>> tasksDTO = taches.stream().map(t -> {
            Map<String, Object> taskDTO = new HashMap<>();
            taskDTO.put("id", t.getId());
            taskDTO.put("titre", t.getTitre());
            taskDTO.put("description", t.getDescription());
            taskDTO.put("dateLimite", t.getDateLimite());
            taskDTO.put("etat", t.getEtat());
            taskDTO.put("dateCreation", t.getDateCreation());
            taskDTO.put("dateFin", t.getDateFin());
            taskDTO.put("sousTaches", t.getSousTaches().stream().map(st -> {
                Map<String, Object> stDTO = new HashMap<>();
                stDTO.put("id", st.getId());
                stDTO.put("titre", st.getTitre());
                stDTO.put("termine", st.isTermine());
                return stDTO;
            }).collect(Collectors.toList()));
            return taskDTO;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("data", tasksDTO));
    }

    @PostMapping("/projets/{projetId}/taches")
    public ResponseEntity<Map<String, Object>> addTask(
            @PathVariable int projetId,
            @RequestBody Map<String, Object> body) {

        Projet projet = projetRepository.findById(projetId);
        if (projet == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Projet non trouvé"));
        }

        String titre = (String) body.get("titre");
        String description = (String) body.get("description");
        String dateLimiteStr = (String) body.get("dateLimite");
        int membreId = (int) body.get("membreId");

        LocalDate dateLimite = LocalDate.parse(dateLimiteStr);
        Membre membre = membreRepository.findById(membreId).orElse(null);
        if (membre == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Membre non trouvé"));
        }

        Tache tache = new Tache(titre, description, dateLimite, projet, Collections.singletonList(membre));
        tacheRepository.save(tache);

        return ResponseEntity.ok(Map.of("id", tache.getId()));
    }

    @DeleteMapping("/taches/{id}")
    public ResponseEntity<Map<String, Object>> deleteTask(@PathVariable int id) {
        Tache tache = tacheRepository.findById(id).orElse(null);
        if (tache == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tâche non trouvée"));
        }
        tacheRepository.delete(tache);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PutMapping("/taches/{id}")
    public ResponseEntity<Map<String, Object>> updateTask(
            @PathVariable int id,
            @RequestBody Map<String, Object> body) {

        Tache tache = tacheRepository.findById(id).orElse(null);
        if (tache == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tâche non trouvée"));
        }

        if (body.containsKey("titre")) {
            tache.setTitre((String) body.get("titre"));
        }
        if (body.containsKey("description")) {
            tache.setDescription((String) body.get("description"));
        }
        if (body.containsKey("etat")) {
            tache.setEtat((String) body.get("etat"));
        }
        if (body.containsKey("dateLimite")) {
            tache.setDateLimite(LocalDate.parse((String) body.get("dateLimite")));
        }

        tacheRepository.save(tache);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ==================== Gestion des membres ====================

    @GetMapping("/projets/{projetId}/membres")
    public ResponseEntity<Map<String, Object>> getMembersByProject(@PathVariable int projetId) {
        Projet projet = projetRepository.findById(projetId);
        if (projet == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Projet non trouvé"));
        }

        List<Membre> membres = membreRepository.findByProjetId(projetId);
        List<Map<String, Object>> membersDTO = membres.stream().map(m -> {
            Map<String, Object> mDTO = new HashMap<>();
            mDTO.put("id", m.getId());
            mDTO.put("nom", m.getNom());
            mDTO.put("prenom", m.getPrenom());
            mDTO.put("email", m.getEmail());
            mDTO.put("telephone", m.getUser() != null ? m.getUser().getTelephone() : "N/A");
            mDTO.put("dateRejointe", m.getDateRejointe());
            mDTO.put("role", m.getRole());
            mDTO.put("type", m.getType());
            return mDTO;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("data", membersDTO));
    }

    @DeleteMapping("/membres/{id}")
    public ResponseEntity<Map<String, Object>> removeMember(@PathVariable int id) {
        Membre membre = membreRepository.findById(id).orElse(null);
        if (membre == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Membre non trouvé"));
        }
        membreRepository.delete(membre);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
