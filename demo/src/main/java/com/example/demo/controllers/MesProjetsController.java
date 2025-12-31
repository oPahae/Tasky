package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.models.Projet;
import com.example.demo.models.Tache;
import com.example.demo.models.User;
import com.example.demo.hooks.MembreDTO;
import com.example.demo.hooks.ProjetDTO;
import com.example.demo.models.Membre;
import com.example.demo.repositories.ProjetRepository;
import com.example.demo.repositories.MembreRepository;
import com.example.demo.repositories.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Date;

@RestController
@RequestMapping("/api")
public class MesProjetsController {
    @Autowired
    private ProjetRepository projetRepository;
    @Autowired
    private MembreRepository membreRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("projet/all")
    public List<ProjetDTO> getAllProjet() {
        return projetRepository.findAll().stream()
                .map(this::convertToProjetDTO)
                .collect(Collectors.toList());
    }

    @DeleteMapping("projet/deleteAll")
    public void deleteAllProject() {
        projetRepository.deleteAll();
    }

    @DeleteMapping("projet/delete/{id}")
    public void deleteProjet(@PathVariable int id) {
        projetRepository.deleteById(id);
    }

    @PostMapping("projet/add")
    public void addProjet(@RequestBody Projet p) {
        projetRepository.save(p);
    }

    @GetMapping("/projet/membre/{id}")
    public List<ProjetDTO> projetByIdMembre(@PathVariable int id) {
        return projetRepository.findByMembres_Id(id).stream()
                .map(this::convertToProjetDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/projet/user/{id}")
    public List<ProjetDTO> getProjetsByUserId(@PathVariable int id) {
        List<Membre> membres = membreRepository.findByUser_Id(id);
        return membres.stream()
                .map(membre -> {
                    ProjetDTO projetDTO = convertToProjetDTO(membre.getProjet());
                    projetDTO.membreID = membre.getId();
                    projetDTO.estResponsable = membre.getRole().equals("Responsable");
                    return projetDTO;
                })
                .collect(Collectors.toList());
    }

    @PutMapping("projet/modifier/{id}")
    public boolean updateProjetById(@PathVariable int id, @RequestBody Projet pr) {
        Projet pr1 = projetRepository.findById(id);
        if (pr1 != null) {
            pr1.setNom(pr.getNom());
            pr1.setDescription(pr.getDescription());
            pr1.setBudget(pr.getBudget());
            pr1.setBudgetConsomme(pr.getBudgetConsomme());
            pr1.setCode(pr.getCode());
            pr1.setDeadline(pr.getDeadline());
            pr1.setDateFin(pr.getDateFin());
            projetRepository.save(pr1);
            return true;
        }
        return false;
    }

    @GetMapping("projet/{id}/Membre")
    public List<MembreDTO> getMembreProjet(@PathVariable int id) {
        Projet pr = projetRepository.findById(id);
        List<Membre> m = pr.getMembres();
        if (pr != null && m != null) {
            return m.stream().map(this::convertToMembreDTO).collect(Collectors.toList());
        }
        return null;
    }

    private ProjetDTO convertToProjetDTO(Projet t) {
        if (t == null)
            return null;
        ProjetDTO dto = new ProjetDTO();
        dto.id = t.getId();
        dto.nom = (t.getNom());
        dto.code = (t.getCode());
        dto.description = (t.getDescription());
        dto.dateDebut = (t.getDateDebut());
        dto.dateFin = (t.getDateFin());
        dto.deadline = (t.getDeadline());
        dto.budget = (t.getBudget());
        dto.budgetConsomme = (t.getBudgetConsomme());

        return dto;
    }

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

    @GetMapping("/projet/{id}/progress")
    public double getProgressProjet(@PathVariable int id) {
        Projet p = projetRepository.findById(id);
        if (p != null) {
            List<Tache> taches = p.getTaches();
            if (taches.size() == 0) {
                return 0;
            }
            double total = 0;
            total = taches.stream()
                    .filter(t -> "terminé".equalsIgnoreCase(t.getEtat()))
                    .count();
            return (total / taches.size()) * 100;
        }
        return 0;
    }

    @GetMapping("projet/{idProjet}/addmembre/{idMembre}")
    public boolean rejoindreAuProjet(@PathVariable("idProjet") int id, @PathVariable("idMembre") int idm) {
        Projet p = projetRepository.findById(id);
        Membre m = membreRepository.findById(idm).orElse(null);
        if (p != null) {
            p.getMembres().add(m);
            projetRepository.save(p);
            return true;
        }
        return false;
    }

    @PostMapping("projet/creer/{userId}")
    public ProjetDTO createProjet(@PathVariable int userId, @RequestBody ProjetDTO dto) {
        Projet p = new Projet();
        p.setNom(dto.nom);
        p.setCode(dto.code);
        p.setDescription(dto.description);
        p.setBudget(dto.budget);
        p.setBudgetConsomme(dto.budgetConsomme);
        p.setDeadline(dto.deadline);
        p.setDateDebut(dto.dateDebut);
        p.setDateFin(dto.dateFin);
        projetRepository.save(p);

        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            Membre membre = new Membre();
            membre.setNom(user.getNom());
            membre.setPrenom(user.getPrenom());
            membre.setEmail(user.getEmail());
            membre.setDescription("Créateur du projet");
            membre.setRole("Responsable");
            membre.setType("-");
            membre.setUser(user);
            membre.setProjet(p);
            membre.setDateRejointe(new Date());
            membreRepository.save(membre);
        }

        return convertToProjetDTO(p);
    }

    @GetMapping("projet/join/{code}/{userId}")
    public Map<String, Object> joinProjectByCode(@PathVariable String code, @PathVariable int userId) {
        Map<String, Object> response = new HashMap<>();
        Projet projet = projetRepository.findByCode(code);
        if (projet == null) {
            response.put("success", false);
            response.put("message", "Code de projet invalide");
            return response;
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            response.put("success", false);
            response.put("message", "Utilisateur introuvable");
            return response;
        }

        boolean alreadyMember = projet.getMembres().stream()
                .anyMatch(membre -> membre.getUser().getId() == userId);
        if (alreadyMember) {
            response.put("success", false);
            response.put("message", "Vous êtes déjà membre de ce projet");
            return response;
        }

        Membre membre = new Membre();
        membre.setNom(user.getNom());
        membre.setPrenom(user.getPrenom());
        membre.setEmail(user.getEmail());
        membre.setDescription("Membre depuis " + new Date());
        membre.setRole("Membre");
        membre.setType("Développeur");
        membre.setUser(user);
        membre.setProjet(projet);
        membre.setDateRejointe(new Date());

        membreRepository.save(membre);

        response.put("success", true);
        response.put("message", "Vous avez rejoint le projet avec succès");
        response.put("projet", convertToProjetDTO(projet));
        return response;
    }
}