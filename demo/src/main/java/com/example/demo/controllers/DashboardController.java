package com.example.demo.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.demo.models.Projet;
import com.example.demo.models.SousTache;
import com.example.demo.models.Tache;
import com.example.demo.hooks.MembreDTO;
import com.example.demo.hooks.TacheDTO;
import com.example.demo.models.Membre;
import com.example.demo.repositories.ProjetRepository;
import com.example.demo.repositories.TacheRepository;



import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class DashboardController {
     @Autowired
    private ProjetRepository projetRepository;
    private TacheRepository tacheRepository;

    @GetMapping("dashboard/projet/{id}/Membrenbr")
    public int getNbrMembreProjet(int id){
        Projet pr=projetRepository.findById(id);
        int nbr=pr.getMembres().size();
       return nbr;
    }
    @GetMapping("dashboard/projet/{id}/encours")
    public int getNbrProjetEnCours(int id){
        Projet pr=projetRepository.findById(id);
        List<Tache>taches=pr.getTaches();
        long nbr = taches.stream()
            .filter(t -> "En cours".equalsIgnoreCase(t.getEtat()))
            .count();

                    return (int)nbr;
       
    }
    @GetMapping("dashboard/projet/{id}/terminer")
    public int getNbrProjetTerminer(int id){
        Projet pr=projetRepository.findById(id);
        List<Tache>taches=pr.getTaches();
        long nbr = taches.stream()
            .filter(t -> "Terminée".equalsIgnoreCase(t.getEtat()))
            .count();

                    return (int)nbr;
       
    }
    @GetMapping("dashboard/projet/{id}/nbrtache")
    public int getNbrTacheProjet(int id){
        Projet pr=projetRepository.findById(id);
        List<Tache>taches=pr.getTaches();
                    return taches.size();   
    }
    
 private TacheDTO convertToTacheDTO(Tache t) {
        if (t == null) return null;

        TacheDTO dto = new TacheDTO();
        dto.id=t.getId();
        dto.titre=(t.getTitre());
        dto.description=(t.getDescription());
        dto.etat=(t.getEtat());
        dto.dateCreation=(t.getDateCreation());
        dto.dateFin=(t.getDateFin());
        dto.dateLimite=(t.getDateLimite());


        return dto;}
    @GetMapping("dashboard/projet/{id}/taches")
    public List<TacheDTO> getTachesProjet(int id){
        Projet pr=projetRepository.findById(id);
         if(pr!=null) {
                return pr.getTaches()
                .stream()
                .map(this::convertToTacheDTO)
                .collect(Collectors.toList());
         }
         return null;
    }

     @GetMapping("dashboard/taches/{id}/progress")
     public double getProgress(@PathVariable int id)
     { Tache t=tacheRepository.findById(id).orElse(null); 
        if(t!=null){ List <SousTache> staches=t.getSousTaches(); 
            if(staches.size()==0){ return 0; } 
            double total=0; 
            total=staches.stream().filter(SousTache::isTermine)
            .count(); 
            return (total/staches.size())*100; } return 0; }


     @GetMapping("dashboard/projet/{id}/Membre")
    public List<MembreDTO> getMembreProjet(int id){
        Projet pr=projetRepository.findById(id);
        List<Membre>m= pr.getMembres();
        if(pr!=null && m!=null){
               return m.stream().map(this::convertToMembreDTO).collect(Collectors.toList());
        }
        return null;
     
    }



    private MembreDTO convertToMembreDTO(Membre t) {
        if (t == null) return null;
        MembreDTO dto = new MembreDTO();
        dto.id=t.getId();
        dto.nom=(t.getNom());
        dto.email=(t.getEmail());
        dto.description=(t.getDescription());
        dto.password=(t.getPassword());
        dto.role=(t.getRole());
        dto.type=(t.getType());


        return dto;}

    
}