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
        Tache tache = tacheRepository.findById(id).orElseThrow(() -> new RuntimeException("Tâche non trouvée"));
        List<SousTache> sousTaches = sousTacheRepository.findByTacheId(id);
        List<Document> documents = documentRepository.findByTacheId(id);
        List<Commentaire> commentaires = commentaireRepository.findByTacheId(id);
        Map<String, Object> response = new HashMap<>();
        response.put("tache", tache);
        response.put("sousTaches", sousTaches);
        response.put("documents", documents);
        response.put("commentaires", commentaires);
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
        SousTache sousTache = sousTacheRepository.findById(sousTacheId).orElseThrow(() -> new RuntimeException("Sous-tâche non trouvée"));
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
}