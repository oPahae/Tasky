package com.example.demo.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.hooks.MembreDTO;
import com.example.demo.hooks.TacheDTO;
import com.example.demo.models.Membre;
import com.example.demo.models.SousTache;
import com.example.demo.models.Tache;
import com.example.demo.repositories.MembreRepository;
import com.example.demo.repositories.TacheRepository;


@RestController
@RequestMapping("/api/membre")
@CrossOrigin(origins = "*")  // bach swing i9DER i3eyet 3la les requettes
public class MemberController {

    @Autowired
    private MembreRepository membreRepository;

    @Autowired
    private TacheRepository tacheRepository;

    // recuperer les info du membre d'un utilisateur dans un projet
    @GetMapping("/user/{userId}/projet/{projetId}")
    public ResponseEntity<Map<String, Object>> getMembreByUserAndProjet(
            @PathVariable int userId, 
            @PathVariable int projetId) {
        
        System.out.println("Recherche membre pour userId=" + userId + ", projetId=" + projetId);
        
        // Chercher le membre correspondant
        Membre membre = membreRepository.findByUserIdAndProjetId(userId, projetId);
        
        if (membre == null) {
            System.err.println("Aucun membre trouve pour userId=" + userId + " et projetId=" + projetId);
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", membre.getId());
        response.put("nom", membre.getNom());
        response.put("prenom", membre.getPrenom());
        response.put("email", membre.getEmail());
        response.put("role", membre.getRole());
        response.put("type", membre.getType());
        
        System.out.println("Membre trouve: ID=" + membre.getId() + ", nom=" + membre.getPrenom() + " " + membre.getNom());
        
        return ResponseEntity.ok(response);
    }

    // recuperer linfos dyal membre ules taches u progress
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getMembreWithTaches(@PathVariable int id) {
        Optional<Membre> membreOpt = membreRepository.findById(id);
        if (!membreOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Membre membre = membreOpt.get();
        List<Tache> taches = tacheRepository.findByMembresContaining(membre);

        // Calculer le progress pour chaque tache
        List<TacheDTO> tacheDTOs = taches.stream().map(tache -> {
            List<SousTache> sousTaches = tache.getSousTaches();
            int totalSousTaches = sousTaches.size();
            int sousTachesTerminees = (int) sousTaches.stream().filter(SousTache::isTermine).count();
            int progres = totalSousTaches > 0 ? (sousTachesTerminees * 100 / totalSousTaches) : 0;

            return new TacheDTO(
                tache.getId(),
                tache.getTitre(),
                tache.getDescription(),
                tache.getDateLimite(),
                tache.getEtat(),
                tache.getDateCreation(),
                tache.getDateFin(),
                progres
            );
        }).collect(Collectors.toList());

        MembreDTO membreDTO = new MembreDTO(
            membre.getNom(),
            membre.getPrenom(),
            membre.getEmail(),
            membre.getDescription(),
            membre.getRole(),
            membre.getType(),
            membre.getDateRejointe()
        );
        membreDTO.id = membre.getId();

        return ResponseEntity.ok(new Object() {
            public MembreDTO membre = membreDTO;
            public List<TacheDTO> taches = tacheDTOs;
        });
    }

    // Modifier le role et le type dun membre
    @PutMapping("/update/{id}")
    public ResponseEntity<MembreDTO> updateMembre(@PathVariable int id, @RequestBody MembreDTO membreDTO) {
        Optional<Membre> membreOpt = membreRepository.findById(id);
        if (!membreOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Membre membre = membreOpt.get();
        if (membreDTO.role != null) {
            membre.setRole(membreDTO.role);
        }
        if (membreDTO.type != null) {
            membre.setType(membreDTO.type);
        }

        membreRepository.save(membre);

        MembreDTO updatedDTO = new MembreDTO(
            membre.getNom(),
            membre.getPrenom(),
            membre.getEmail(),
            membre.getDescription(),
            membre.getRole(),
            membre.getType(),
            membre.getDateRejointe()
        );
        updatedDTO.id = membre.getId();

        return ResponseEntity.ok(updatedDTO);
        
    }

    // Supprimer un membre
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMembre(@PathVariable int id) {
        if (!membreRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        membreRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}