package com.example.demo.controllers;

import com.example.demo.models.*;
import com.example.demo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/tache")
public class TacheController {
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
    public ResponseEntity<Map<String, Object>> getTacheData(@PathVariable int id) {
        Tache tache = tacheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));

        List<SousTache> sousTaches = sousTacheRepository.findByTacheId(id);
        List<Document> documents = documentRepository.findByTacheId(id);
        List<Commentaire> commentaires = commentaireRepository.findByTacheId(id);

        // DTO Tache
        TacheDTO tacheDTO = new TacheDTO(
                tache.getId(),
                tache.getTitre(),
                tache.getDescription(),
                tache.getDateLimite(),
                tache.getEtat(),
                tache.getDateCreation(),
                tache.getDateFin());

        // DTO SousTache
        List<SousTacheDTO> sousTachesDTO = sousTaches.stream()
                .map(st -> new SousTacheDTO(st.getId(), st.getTitre(), st.isTermine()))
                .toList();

        // DTO Document
        List<DocumentDTO> documentsDTO = documents.stream()
                .map(d -> new DocumentDTO(d.getId(), d.getNom(), d.getDescription()))
                .toList();

        // DTO Commentaire
        List<CommentaireDTO> commentairesDTO = commentaires.stream()
                .map(c -> new CommentaireDTO(
                        c.getId(),
                        "Anonyme",
                        c.getContenu(),
                        c.getDateCreation()))
                .toList();

        // Wrapper final comme attendu par le frontend
        Map<String, Object> tacheWrapper = new HashMap<>();
        tacheWrapper.put("tache", tacheDTO);
        tacheWrapper.put("sousTaches", sousTachesDTO);
        tacheWrapper.put("documents", documentsDTO);
        tacheWrapper.put("commentaires", commentairesDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("tache", tacheWrapper);

        return ResponseEntity.ok(response);
    }

    // Ajouter une sous-tâche
    @PostMapping("/{id}/sous-tache")
    public ResponseEntity<SousTache> addSousTache(@PathVariable int id, @RequestBody Map<String, String> payload) {
        Tache tache = tacheRepository.findById(id).orElseThrow(() -> new RuntimeException("Tâche non trouvée"));
        SousTache sousTache = new SousTache();
        sousTache.setTitre(payload.get("titre"));
        sousTache.setTermine(false);
        sousTache.setDateCreation(LocalDate.now());
        sousTache.setTache(tache);
        SousTache saved = sousTacheRepository.save(sousTache);
        return ResponseEntity.ok(saved);
    }

    // Cocher/décocher une sous-tâche
    @PutMapping("/sous-tache/{sousTacheId}")
    public ResponseEntity<SousTache> toggleSousTache(@PathVariable int sousTacheId) {
        SousTache sousTache = sousTacheRepository.findById(sousTacheId)
                .orElseThrow(() -> new RuntimeException("Sous-tâche non trouvée"));
        sousTache.setTermine(!sousTache.isTermine());
        SousTache updated = sousTacheRepository.save(sousTache);
        return ResponseEntity.ok(updated);
    }

    // Ajouter un document
    @PostMapping("/{id}/document")
    public ResponseEntity<Document> addDocument(@PathVariable int id, @RequestBody Map<String, String> payload) {
        Tache tache = tacheRepository.findById(id).orElseThrow(() -> new RuntimeException("Tâche non trouvée"));
        Document document = new Document();
        document.setNom(payload.get("nom"));
        document.setDescription(payload.get("description"));
        document.setDateCreation(LocalDate.now());
        document.setTache(tache);
        Document saved = documentRepository.save(document);
        return ResponseEntity.ok(saved);
    }

    // Ajouter un commentaire
    @PostMapping("/{id}/commentaire")
    public ResponseEntity<Commentaire> addCommentaire(@PathVariable int id, @RequestBody Map<String, String> payload) {
        Tache tache = tacheRepository.findById(id).orElseThrow(() -> new RuntimeException("Tâche non trouvée"));
        Commentaire commentaire = new Commentaire();
        commentaire.setContenu(payload.get("contenu"));
        commentaire.setDateCreation(LocalDate.now());
        commentaire.setTache(tache);
        Commentaire saved = commentaireRepository.save(commentaire);
        return ResponseEntity.ok(saved);
    }

    // Ajouter un bloquage
    @PostMapping("/{id}/blocage")
    public ResponseEntity<Blocage> addBlocage(@PathVariable int id, @RequestBody Map<String, String> payload) {
        Tache tache = tacheRepository.findById(id).orElseThrow(() -> new RuntimeException("Tâche non trouvée"));
        Blocage blocage = new Blocage();
        blocage.setDescription(payload.get("description"));
        blocage.setDateSignalement(LocalDate.now());
        blocage.setStatut("Signale");
        blocage.setTache(tache);
        Blocage saved = blocageRepository.save(blocage);
        return ResponseEntity.ok(saved);
    }

    // Ajouter une dépense
    @PostMapping("/{id}/depense")
    public ResponseEntity<Tache> addDepense(@PathVariable int id, @RequestBody Map<String, Integer> payload) {
        Tache tache = tacheRepository.findById(id).orElseThrow(() -> new RuntimeException("Tâche non trouvée"));
        int montant = payload.get("montant");
        tache.setDepense(tache.getDepense() + montant);
        Tache updated = tacheRepository.save(tache);
        return ResponseEntity.ok(updated);
    }

    // ---------------- DTO internes ----------------
    private static class TacheDTO {
        public int id;
        public String titre;
        public String description;
        public LocalDate dateLimite;
        public String etat;
        public LocalDate dateCreation;
        public LocalDate dateFin;

        public TacheDTO(int id, String titre, String description, LocalDate dateLimite, String etat,
                LocalDate dateCreation, LocalDate dateFin) {
            this.id = id;
            this.titre = titre;
            this.description = description;
            this.dateLimite = dateLimite;
            this.etat = etat;
            this.dateCreation = dateCreation;
            this.dateFin = dateFin;
        }
    }

    private static class SousTacheDTO {
        public int id;
        public String titre;
        public boolean termine;

        public SousTacheDTO(int id, String titre, boolean termine) {
            this.id = id;
            this.titre = titre;
            this.termine = termine;
        }
    }

    private static class DocumentDTO {
        public int id;
        public String nom;
        public String description;

        public DocumentDTO(int id, String nom, String description) {
            this.id = id;
            this.nom = nom;
            this.description = description;
        }
    }

    private static class CommentaireDTO {
        public int id;
        public String author;
        public String contenu;
        public LocalDate dateCreation;

        public CommentaireDTO(int id, String author, String contenu, LocalDate dateCreation) {
            this.id = id;
            this.author = author;
            this.contenu = contenu;
            this.dateCreation = dateCreation;
        }
    }
}