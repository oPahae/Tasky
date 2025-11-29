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
import com.example.demo.models.Membre;
import com.example.demo.models.Message;
import com.example.demo.models.Blocage;
import com.example.demo.repositories.MembreRepository;
import com.example.demo.repositories.DocumentRepository;
import com.example.demo.repositories.ProjetRepository;
import com.example.demo.repositories.TacheRepository;
import com.example.demo.repositories.BlocageRepository;

import java.util.List;
import java.lang.reflect.Member;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class MesProjetsController {
    @Autowired
    private ProjetRepository projetRepository;
     @Autowired
    private TacheRepository tacheRepository;
    @Autowired
    private MembreRepository membreRepository;

    @GetMapping("Projet/all")
    public List<Projet> getAllProjet(){
           return projetRepository.findAll();
    }
    @DeleteMapping("Projet/deleteAll")
    public void deleteAllProject(){
        projetRepository.deleteAll();
    }
    @DeleteMapping("Projet/delete/{id}")
     public void deleteProjet(int id){
        projetRepository.deleteById(id);
     }
     @PostMapping("Projet/add")
     public void addProjet(Projet p){
        projetRepository.save(p);
     }
     @GetMapping("/Projet/membre/{id}")
     public List<Projet> projetByIdMembre(@PathVariable int id) {
    return projetRepository.findByMembresId(id);
        }
    
    @PutMapping("Projet/modifier/{id}")
    public boolean updateProjetById(@PathVariable int id, Projet pr){
                    Projet pr1 = projetRepository.findById(id);
                    if(pr1 != null){
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
    @GetMapping("Projet/{id}")
    public Projet projetById(int id){
        return projetRepository.findById(id);
    }
    @GetMapping("Projet/nom")
    public Projet projetByNom(String nom){
        return projetRepository.findByNom(nom);
    }
    @GetMapping("Projet/{id}/taches")
    public List<Tache> tachedeProjet(int id){
        Projet pr=projetRepository.findById(id);
        return pr.getTaches();
    }
    @GetMapping("Projet/{id}/membres")
    public List<Membre> membresdeProjet(int id){
        Projet pr=projetRepository.findById(id);
        return pr.getMembres();
    }
    @GetMapping("/Projet/{id}/progress")
    public double getProgressProjet(@PathVariable int id){
        Projet p=projetRepository.findById(id);
        if(p!=null){
           List <Tache> taches=p.getTaches();
           if(taches.size()==0){
            return 0;
           }
              double total=0;
              total=taches.stream()
                .filter(t -> "terminé".equalsIgnoreCase(t.getEtat()))
                .count();
              return (total/taches.size())*100;
        }
        return 0;
    }
      @GetMapping("/Projet/{id}/messages")
     public List<Message> messagesdeProjet(int id){
        Projet pr=projetRepository.findById(id);
        return pr.getMessages();
    }


    

    
}
