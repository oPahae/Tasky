package com.example.demo.controllers;

import com.example.demo.models.*;
import com.example.demo.repositories.TacheRepository;
import com.example.demo.repositories.SousTacheRepository;
import com.example.demo.repositories.DocumentRepository;
import com.example.demo.repositories.CommentaireRepository;
import com.example.demo.repositories.BlocageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tache")
public class TacheController2 {

    @Autowired
    private TacheRepository tacheRepository;

    @Autowired
    private SousTacheRepository sousTacheRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private CommentaireRepository commentaireRepository;

    @Autowired
    private BlocageRepository blocageRepository;

    // Récupérer toutes les données de la tâche actuelle
    @GetMapping("/{id}")
    public ResponseEntity<?> getTacheData(@PathVariable int id) {
        Optional<Tache> tacheOpt = tacheRepository.findById(id);
        if (!tacheOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Tache tache = tacheOpt.get();
        return ResponseEntity.ok(new TacheDTO(tache));
    }

    // Ajouter une sous-tâche
    @PostMapping("/{id}/sous-tache")
    public ResponseEntity<?> addSousTache(@PathVariable int id, @RequestBody SousTacheDTO sousTacheDTO) {
        Optional<Tache> tacheOpt = tacheRepository.findById(id);
        if (!tacheOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        SousTache sousTache = new SousTache();
        sousTache.setTitre(sousTacheDTO.getTitre());
        sousTache.setTermine(false);
        sousTache.setDateCreation(LocalDateTime.now());
        sousTache.setTache(tacheOpt.get());
        sousTacheRepository.save(sousTache);
        return ResponseEntity.ok().build();
    }

    // Cocher/décocher une sous-tâche
    @PutMapping("/sous-tache/{sousTacheId}")
    public ResponseEntity<?> toggleSousTache(@PathVariable int sousTacheId) {
        Optional<SousTache> sousTacheOpt = sousTacheRepository.findById(sousTacheId);
        if (!sousTacheOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        SousTache sousTache = sousTacheOpt.get();
        sousTache.setTermine(!sousTache.isTermine());
        sousTacheRepository.save(sousTache);
        return ResponseEntity.ok().build();
    }

    // Ajouter un document
    @PostMapping("/{id}/document")
    public ResponseEntity<?> addDocument(@PathVariable int id, @RequestBody DocumentDTO documentDTO) {
        Optional<Tache> tacheOpt = tacheRepository.findById(id);
        if (!tacheOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Document document = new Document();
        document.setNom(documentDTO.getNom());
        document.setDescription(documentDTO.getDescription());
        document.setDateCreation(LocalDate.now());
        document.setTache(tacheOpt.get());
        documentRepository.save(document);
        return ResponseEntity.ok().build();
    }

    // Ajouter un commentaire
    @PostMapping("/{id}/commentaire")
    public ResponseEntity<?> addCommentaire(@PathVariable int id, @RequestBody CommentaireDTO commentaireDTO) {
        Optional<Tache> tacheOpt = tacheRepository.findById(id);
        if (!tacheOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Commentaire commentaire = new Commentaire();
        commentaire.setContenu(commentaireDTO.getContenu());
        commentaire.setDateCreation(LocalDate.now());
        commentaire.setTache(tacheOpt.get());
        commentaireRepository.save(commentaire);
        return ResponseEntity.ok().build();
    }

    // Ajouter un bloquage
    @PostMapping("/{id}/blocage")
    public ResponseEntity<?> addBlocage(@PathVariable int id, @RequestBody BlocageDTO blocageDTO) {
        Optional<Tache> tacheOpt = tacheRepository.findById(id);
        if (!tacheOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Blocage blocage = new Blocage();
        blocage.setDescription(blocageDTO.getDescription());
        blocage.setDateSignalement(LocalDate.now());
        blocage.setStatut("Signalé");
        blocage.setTache(tacheOpt.get());
        blocageRepository.save(blocage);
        return ResponseEntity.ok().build();
    }

    // Ajouter une dépense (exemple simplifié)
    @PostMapping("/{id}/depense")
    public ResponseEntity<?> addDepense(@PathVariable int id, @RequestBody DepenseDTO depenseDTO) {
        // Logique pour ajouter une dépense (à adapter selon votre modèle)
        return ResponseEntity.ok().build();
    }

    // DTOs
    public static class TacheDTO {
        private int id;
        private String titre;
        private String description;
        private LocalDate dateCreation;
        private LocalDate dateLimite;
        private List<SousTacheDTO> sousTaches;
        private List<DocumentDTO> documents;
        private List<CommentaireDTO> commentaires;

        public TacheDTO(Tache tache) {
            this.id = tache.getId();
            this.titre = tache.getTitre();
            this.description = tache.getDescription();
            this.dateCreation = tache.getDateCreation();
            this.dateLimite = tache.getDateLimite();
            this.sousTaches = tache.getSousTaches().stream().map(SousTacheDTO::new).collect(Collectors.toList());
            this.documents = tache.getDocuments().stream().map(DocumentDTO::new).collect(Collectors.toList());
            this.commentaires = tache.getCommentaires().stream().map(CommentaireDTO::new).collect(Collectors.toList());
        }

        // Getters
        public int getId() { return id; }
        public String getTitre() { return titre; }
        public String getDescription() { return description; }
        public LocalDate getDateCreation() { return dateCreation; }
        public LocalDate getDateLimite() { return dateLimite; }
        public List<SousTacheDTO> getSousTaches() { return sousTaches; }
        public List<DocumentDTO> getDocuments() { return documents; }
        public List<CommentaireDTO> getCommentaires() { return commentaires; }
    }

    public static class SousTacheDTO {
        private int id;
        private String titre;
        private boolean termine;

        public SousTacheDTO(SousTache sousTache) {
            this.id = sousTache.getId();
            this.titre = sousTache.getTitre();
            this.termine = sousTache.isTermine();
        }

        // Getters
        public int getId() { return id; }
        public String getTitre() { return titre; }
        public boolean isTermine() { return termine; }
    }

    public static class DocumentDTO {
        private int id;
        private String nom;
        private String description;

        public DocumentDTO(Document document) {
            this.id = document.getId();
            this.nom = document.getNom();
            this.description = document.getDescription();
        }

        // Getters
        public int getId() { return id; }
        public String getNom() { return nom; }
        public String getDescription() { return description; }
    }

    public static class CommentaireDTO {
        private int id;
        private String contenu;
        private LocalDate dateCreation;

        public CommentaireDTO(Commentaire commentaire) {
            this.id = commentaire.getId();
            this.contenu = commentaire.getContenu();
            this.dateCreation = commentaire.getDateCreation();
        }

        // Getters
        public int getId() { return id; }
        public String getContenu() { return contenu; }
        public LocalDate getDateCreation() { return dateCreation; }
    }

    public static class BlocageDTO {
        private String description;

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class DepenseDTO {
        private double montant;
        private String description;

        public double getMontant() { return montant; }
        public void setMontant(double montant) { this.montant = montant; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}