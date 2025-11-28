package com.example.demo.controllers;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.models.SousTache;
import com.example.demo.models.Tache;
import com.example.demo.repository.TacheRepository;
import com.example.demo.models.Commentaire;
import com.example.demo.models.Document;
import com.example.demo.models.Blockage;
import java.time.LocalDateTime;

import java.util.List;

public class TachesController {
     private TacheRepository tacheRepository;

    public TachesController(TacheRepository tacheRepository) {
        this.tacheRepository = tacheRepository;}

     @GetMapping("/taches/{id}/staches")
    public List<SousTache> getSousTaches(@PathVariable int id){
        Tache t=tacheRepository.findById(id).orElse(null);
        if(t!=null){
            return t.getSousTaches();
        }
        return null;
    }
     @GetMapping("/taches/membre/{id}")
    public List<Tache> getTachesByMembreId(@PathVariable int id){
        return tacheRepository.findByMembreId(id);
    }
    @GetMapping("/taches/projet/{id}")
    public List<Tache> getTachesByProjetId(@PathVariable int id){
        return tacheRepository.findByProjetId(id);
    }
    @GetMapping("/taches/{id}/commentaire")
    public List<Commentaire> getCommentairesByTacheId(@PathVariable int id){
        Tache t=tacheRepository.findById(id).orElse(null);
        if(t!=null){
            return t.getCommentaires();
        }
        return null;
    }
    @GetMapping("/taches/{id}/documents")
    public List<Document> getDocumentsByTacheId(@PathVariable int id){
        Tache t=tacheRepository.findById(id).orElse(null);
        if(t!=null){
            return t.getDocuments();
        }
        return null;
    }
    @GetMapping("/taches/{id}/blockage")
    public List<Blockage> getBlockagesByTacheId(@PathVariable int id){
        Tache t=tacheRepository.findById(id).orElse(null);
        if(t!=null){
            return t.getBlockage();
        }
        return null;
    }
    @PutMapping("/taches/{id}/etat")
    public void updateEtat(@PathVariable int id, String etat){
        Tache t=tacheRepository.findById(id).orElse(null);
        if(t!=null){
            t.setEtat(etat);
            tacheRepository.save(t);
        }
    }
    @PutMapping("/taches/{id}/finir")
    public boolean finirTache(@PathVariable int id){
        Tache t=tacheRepository.findById(id).orElse(null);
        List <SousTache> staches=t.getSousTaches();

        if(t!=null && staches.size()>0){
           if(staches.size()==staches.stream().filter(SousTache::getEtat).count()){
           t.setEtat("Terminé");
           t.setDateFinale(LocalDateTime.now());
           tacheRepository.save(t);
           return true;
        }}
        return false;
    }
  
    
    @GetMapping("/taches/{id}/progress")
    public double getProgress(@PathVariable int id){
        Tache t=tacheRepository.findById(id).orElse(null);
        if(t!=null){
           List <SousTache> staches=t.getSousTaches();
           if(staches.size()==0){
            return 0;
           }
              double total=0;
              total=staches.stream().filter(SousTache::getEtat).count();
              return (total/staches.size())*100;
        }
        return 0;
    }

    @PutMapping("/taches/{id}/bloquer")
    public void bloquerTache(@PathVariable int id,@RequestBody  Blockage blockage){
        Tache t=tacheRepository.findById(id).orElse(null);
        if(t!=null){
            List<Blockage> blockages=t.getBlockage();
            blockages.add(blockage);
            t.setBlockage(blockages);
            t.setEtat("Bloquée");
            tacheRepository.save(t);
        }
    }
    @GetMapping("/taches/{id}/cloturer")
    public boolean tacheCloturer(@PathVariable int id){
        Tache st=tacheRepository.findById(id).orElse(null);
        if(st!=null && st.getDateFinale()!=null){
            if(LocalDateTime.now().isAfter(st.getDateLimite()) && !st.getEtat().equals("terminée")){
                st.setEtat("Pas fini la tache est dépassée");
                tacheRepository.save(st);
                return true;
            };
        }
        return false;
    }

    
    
    
}
