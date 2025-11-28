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
@RestController
@RequestMapping("/api")
public class TacheController {
    private TacheRepository tacheRepository;
    public TacheController(TacheRepository tacheRepository) {
        this.tacheRepository = tacheRepository;
    }
    @GetMapping("/tache")
    public List<Tache> getTaches() {
        return tacheRepository.findAll();
    }
    @DeleteMapping("/tache/{id}")
    public void deleteTache(@PathVariable int id) {
        tacheRepository.deleteById(id);
    }
    @PostMapping("/tache/add")
    public void addTache(@RequestBody Tache tache){
        tache.setEtat("en cours");
        tacheRepository.save(tache);
    }
    
       @PutMapping("/tache/{id}")
     public void updateTache(@PathVariable int id,@RequestBody  Tache nouv){
        Tache t=tacheRepository.findById(id).orElse(null);
        if(t!=null){
            t.setTitre(nouv.getTitre());
            t.setDescription(nouv.getDescription());
            t.setDateCreation(nouv.getDateCreation());
            t.setDateFinale(nouv.getDateFinale());
            t.setDateLimite(nouv.getDateLimite());
            t.setSousTaches(nouv.getSousTaches());
            t.setEtat(nouv.getEtat());
            tacheRepository.save(t);
        }}
  @GetMapping("/tache/{id}")
    public Tache getTacheById(@PathVariable int id){
    return tacheRepository.findById(id).orElse(null);
    }
   
    






}
