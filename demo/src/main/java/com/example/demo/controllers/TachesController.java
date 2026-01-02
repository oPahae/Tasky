package com.example.demo.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.hooks.TacheDTO;
import com.example.demo.models.Blocage;
import com.example.demo.models.Membre;
import com.example.demo.models.Notification;
import com.example.demo.models.Projet;
import com.example.demo.models.SousTache;
import com.example.demo.models.Tache;
import com.example.demo.repositories.MembreRepository;
import com.example.demo.repositories.ProjetRepository;
import com.example.demo.repositories.TacheRepository;

      //la gestion dyal taches membre,projets et a manipulation dyal lestaches m3a les membre.
@RestController
@RequestMapping("/api")
public class TachesController {

    private final TacheRepository tacheRepository;
    private final ProjetRepository projetRepository;
    private final MembreRepository membreRepository;

    public TachesController(TacheRepository tacheRepository, MembreRepository membreRepository,ProjetRepository projetRepository) {
        this.tacheRepository = tacheRepository;
        this.membreRepository = membreRepository;
        this.projetRepository = projetRepository;
    }

    //convert tache l dto pour securite
    private TacheDTO convertToTacheDTO( Tache t) {
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

    //affectation tache lun membre et envoie notif 
    @GetMapping("/taches/{idTache}/add/membre/{idMembre}")
    public boolean addtachetoMember(@PathVariable("idTache")int idTache,@PathVariable("idMembre") int id1) {
        Tache t = tacheRepository.findById(idTache).orElse(null);
        Membre m = membreRepository.findById(id1).orElse(null);
        Projet p=t.getProjet();
        if (t != null && m != null) {
            t.getMembres().add(m);
            tacheRepository.save(t);
            p.getNotificationss().add(new Notification("Vous avez été assigné à la tâche: "+t.getTitre()));
            projetRepository.save(p);
            return true;
        }
        return false;
    }

    //recupere tt les taches
    @GetMapping("/taches/all")
    public List<TacheDTO> getTaches() {
        return tacheRepository.findAll()
                .stream()
                .map(this::convertToTacheDTO)
                .collect(Collectors.toList());
    }

    //ajout du nvl tache
    @PostMapping("/taches/add")
    public void addTache(@RequestBody Tache tache) {
        tache.setEtat("en cours");
        tacheRepository.save(tache);
    }

    //recupere une tache specifique
    @GetMapping("/taches/{id}")
    public TacheDTO getTacheById(@PathVariable int id) {
        return tacheRepository.findById(id)
                .map(this::convertToTacheDTO)
                .orElse(null);
    }

    //recupere tt les tahches dyal chi membre
    @GetMapping("/taches/Membre/{id}")
    public List<TacheDTO> getTachesByIdMembre(@PathVariable int id) {
        Membre m = membreRepository.findById(id).orElse(null);
        return m.getTaches().stream().map(this::convertToTacheDTO)
                .collect(Collectors.toList());

    }

    //met a jour etat dyal tache

    @PutMapping("/taches/{id}/etat")
    public void updateEtat(@PathVariable int id, @RequestParam String etat) {
        Tache t = tacheRepository.findById(id).orElse(null);
        if (t != null) {
            t.setEtat(etat);
            tacheRepository.save(t);
        }
    }

    //mettre a jour une tache termine ila ga3 les ss tache dyalha termine
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

    //modification dyal tache specifique
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

    //si une tache bloquer kadar la modification dyal etat dyalha bloque u kat ajota f table dyal bloquage 
    @PutMapping("/taches/{id}/bloquer")
    public void bloquerTache(@PathVariable int id, @RequestBody Blocage blocage) {
        Tache t = tacheRepository.findById(id).orElse(null);
        if (t != null) {
            t.getBlocages().add(blocage);
            t.setEtat("Bloquée");
            tacheRepository.save(t);
        }
    }

    //verification dyal wahed tache wech depassat date limite dyalha si oui eta dyalha katweli depassee
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

    //calcul dyal % de progress dyal tache en fct dyal ss taches temine dyalha
    @GetMapping("/taches/{idTache}/progress")
    public double getProgress(@PathVariable("idTache") int id) {
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
