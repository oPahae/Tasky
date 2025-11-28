package com.example.demo.controllers;
import org.springframework.web.bind.annotation.DeleteMapping;
import com.example.demo.Repository.TacheRepository;
import com.example.demo.Repository.SousTaskRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.models.SousTache;
import com.example.demo.models.Tache;
import java.util.List;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class SousTacheController {
    private SousTaskRepository sousTaskRepository;
    private TacheRepository tacheRepository;
    public SousTacheController(SousTaskRepository sousTaskRepository, TacheRepository tacheRepository) {
        this.sousTaskRepository = sousTaskRepository;
        this.tacheRepository = tacheRepository;
    }
    @GetMapping("/staches")
    public List<SousTache> getSousTaches() {
        return sousTaskRepository.findAll();}

    @DeleteMapping("/staches/{id}")
    public void deleteSousTache(@PathVariable int id) {
        sousTaskRepository.deleteById(id);
    }
    @PostMapping("/staches/add")
    public void addSousTache(@RequestBody SousTache sousTache){
        sousTaskRepository.save(sousTache);

}
    @GetMapping("/staches/{id}")
    public SousTache getSousTacheById(@PathVariable int id){
    return sousTaskRepository.findById(id).orElse(null);
    }
    @PutMapping("/staches/{id}")
     public void updateSousTache(@PathVariable int id,@RequestBody  SousTache nouv){
        SousTache st=sousTaskRepository.findById(id).orElse(null);
        if(st!=null){
            st.setTitre(nouv.getTitre());
            st.setDescription(nouv.getDescription());
            st.setEtat(nouv.getEtat());
            st.setTache(nouv.getTache());
            st.setDateFinale(nouv.getDateFinale());
            st.setDateCreation(nouv.getDateCreation());
            sousTaskRepository.save(st);
        }
    }
    @GetMapping("/staches/tache/{id}")
    public List<SousTache> getSousTachesByTacheId(@PathVariable("id") int tacheId){
        Tache t=tacheRepository.findById(tacheId).orElse(null);
        if(t!=null){
            return t.getSousTaches();
        }
        return null;
    }
    
    @PutMapping("/staches/{id}/close")
    public void closeSousTache(@PathVariable int id){
        SousTache st=sousTaskRepository.findById(id).orElse(null);
        if(st!=null){
            st.setEtat(true);
            st.setDateFinale(LocalDateTime.now());
            sousTaskRepository.save(st);
        }
    }
    @PostMapping("/staches/{id}/tache/{tacheId}")
    public void addSousTacheToTache(@PathVariable int id,@PathVariable int d){
        SousTache st=sousTaskRepository.findById(id).orElse(null);
        Tache t=tacheRepository.findById(d).orElse(null);
        if(st!=null && t!=null){
            st.setTache(t);
            sousTaskRepository.save(st);

        }
    }
    @GetMapping("/staches/{id}/membre")
    public String getMembreOfSousTache(@PathVariable int id){
        SousTache st=sousTaskRepository.findById(id).orElse(null);
        if(st!=null && st.getTache()!=null && st.getTache().getMembre()!=null){
            return st.getTache().getMembre().getNom();
        }
        return null;
    }
    
    @GetMapping("/staches/{id}/finir")
    public boolean sousTacheFinir(@PathVariable int id){
        SousTache st=sousTaskRepository.findById(id).orElse(null);
        if(st!=null){
            st.setEtat(true);
            st.setDateFinale(LocalDateTime.now());
            sousTaskRepository.save(st);
            return true;}
        return false;}

    @GetMapping("/staches/{id}/stachefin")
    public List<SousTache> getSousTachesFinByTacheId(@PathVariable int id){
        Tache t=tacheRepository.findById(id).orElse(null);
        if(t!=null){
            List<SousTache> allStaches=t.getSousTaches();
            return allStaches.stream().filter(SousTache::getEtat).toList();
        }
        return null;
    }
    @GetMapping("/staches/allfin")
    public List<SousTache> getSousTachesfinir(){
        List<SousTache>st= sousTaskRepository.findAll();
        if(st!=null){
        return st.stream().filter(SousTache::getEtat).toList();}
        return null;
    }

    @GetMapping("/staches/allnonfin")
    public List<SousTache> getSousTachesnonfinir(){
        List<SousTache>st= sousTaskRepository.findAll();
        if (st!=null) {
        return st.stream().filter(s -> !s.getEtat()).toList();}
        
        return null;}
    












}