package com.example.demo.controllers;

import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.models.*;
import com.example.demo.repositories.TacheRepository;
import com.example.demo.repositories.MembreRepository;

import com.example.demo.hooks.TacheDTO;

@RestController
@RequestMapping("/api")
public class TachesController {

    private final TacheRepository tacheRepository;
    private final MembreRepository membreRepository;

    public TachesController(TacheRepository tacheRepository, MembreRepository membreRepository) {
        this.tacheRepository = tacheRepository;
        this.membreRepository = membreRepository;
    }

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

        return dto;
    }

    @GetMapping("/taches/add/membre/{id}")
    public boolean addtachetoMember(int idTache, int id1) {
        Tache t = tacheRepository.findById(idTache).orElse(null);
        Membre m = membreRepository.findById(id1).orElse(null);
        if (t != null && m != null) {
            t.getMembres().add(m);
            return true;
        }
        return false;
    }

    @GetMapping("/taches")
    public List<TacheDTO> getTaches() {
        return tacheRepository.findAll()
                .stream()
                .map(this::convertToTacheDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/taches/add")
    public void addTache(@RequestBody Tache tache) {
        tache.setEtat("en cours");
        tacheRepository.save(tache);
    }

    @GetMapping("/taches/{id}")
    public TacheDTO getTacheById(@PathVariable int id) {
        return tacheRepository.findById(id)
                .map(this::convertToTacheDTO)
                .orElse(null);
    }

    @GetMapping("/taches/Membre/{id}")
    public List<TacheDTO> getTachesByIdMembre(@PathVariable int id) {
        Membre m = membreRepository.findById(id).orElse(null);
        return m.getTaches().stream().map(this::convertToTacheDTO)
                .collect(Collectors.toList());

    }

    @PutMapping("/taches/{id}/etat")
    public void updateEtat(@PathVariable int id, @RequestParam String etat) {
        Tache t = tacheRepository.findById(id).orElse(null);
        if (t != null) {
            t.setEtat(etat);
            tacheRepository.save(t);
        }
    }

    @PutMapping("/taches/{id}/finir")
    public boolean finirTache(@PathVariable int id) {
        Tache t = tacheRepository.findById(id).orElse(null);
        if (t == null)
            return false;

        List<SousTache> sts = t.getSousTaches();
        if (sts.size() == 0)
            return false;

        if (sts.stream().allMatch(SousTache::isTermine)) {
            t.setEtat("Terminé");
            t.setDateFin(LocalDate.now());
            tacheRepository.save(t);
            return true;
        }

        return false;
    }

    @PutMapping("/taches/{id}")
    public void updateTache(@PathVariable int id, @RequestBody Tache nouv) {
        Tache t = tacheRepository.findById(id).orElse(null);
        if (t != null) {
            t.setTitre(nouv.getTitre());
            t.setDescription(nouv.getDescription());
            t.setDateCreation(nouv.getDateCreation());
            t.setDateFin(nouv.getDateFin());
            t.setDateLimite(nouv.getDateLimite());
            t.setSousTaches(nouv.getSousTaches());
            t.setEtat(nouv.getEtat());
            tacheRepository.save(t);
        }
    }

    @PutMapping("/taches/{id}/bloquer")
    public void bloquerTache(@PathVariable int id, @RequestBody Blocage blocage) {
        Tache t = tacheRepository.findById(id).orElse(null);
        if (t != null) {
            t.getBlocages().add(blocage);
            t.setEtat("Bloquée");
            tacheRepository.save(t);
        }
    }

    @GetMapping("/taches/{id}/cloturer")
    public boolean tacheCloturer(@PathVariable int id) {
        Tache t = tacheRepository.findById(id).orElse(null);
        if (t != null && t.getDateLimite() != null) {
            if (LocalDate.now().isAfter(t.getDateLimite())
                    && !t.getEtat().equalsIgnoreCase("terminée")) {

                t.setEtat("Pas fini - dépassée");
                tacheRepository.save(t);
                return true;
            }
        }
        return false;
    }

    @GetMapping("/taches/{id}/membre/{id}/progress")
    public double getProgress(@PathVariable int id) {
        Tache t = tacheRepository.findById(id).orElse(null);
        if (t != null) {
            List<SousTache> staches = t.getSousTaches();
            if (staches.size() == 0) {
                return 0;
            }
            double total = 0;
            total = staches.stream().filter(SousTache::isTermine).count();
            return (total / staches.size()) * 100;
        }
        return 0;
    }
}