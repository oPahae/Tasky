package com.example.demo.controllers;

import java.time.LocalDate;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.hooks.BlocageDTO;
import com.example.demo.hooks.CommentaireDTO;
import com.example.demo.hooks.DocumentDTO;
import com.example.demo.hooks.SousTacheDTO;
import com.example.demo.hooks.TacheDTO;
import com.example.demo.models.Blocage;
import com.example.demo.models.Commentaire;
import com.example.demo.models.Document;
import com.example.demo.models.Membre;
import com.example.demo.models.Notification;
import com.example.demo.models.Projet;
import com.example.demo.models.SousTache;
import com.example.demo.models.Tache;
import com.example.demo.repositories.BlocageRepository;
import com.example.demo.repositories.CommentaireRepository;
import com.example.demo.repositories.DocumentRepository;
import com.example.demo.repositories.MembreRepository;
import com.example.demo.repositories.NotificationRepository;
import com.example.demo.repositories.ProjetRepository;
import com.example.demo.repositories.SousTacheRepository;
import com.example.demo.repositories.TacheRepository;

       //La gestion dyal les taches,ss taches,docs,commentaires,blocages,depenses 
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

        @Autowired
        private ProjetRepository projetRepository;

        @Autowired
        private MembreRepository membreRepository;

        @Autowired
        private NotificationRepository notificationRepository;

        //recupere les info dyal tache avc les commantaire ,les sstaches,les docs dyal had tache
        @GetMapping("/{id}")
        public ResponseEntity<Map<String, Object>> getTacheData(@PathVariable int id) {
                Tache tache = tacheRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Tache non trouvee"));
                List<SousTache> sousTaches = sousTacheRepository.findByTacheId(id);
                List<Document> documents = documentRepository.findByTacheId(id);
                List<Commentaire> commentaires = commentaireRepository.findByTacheId(id);

                TacheDTO tacheDTO = new TacheDTO(
                                tache.getId(),
                                tache.getTitre(),
                                tache.getDescription(),
                                tache.getDateLimite(),
                                tache.getEtat(),
                                tache.getDateCreation(),
                                tache.getDateFin(),
                                0);

                List<SousTacheDTO> sousTachesDTO = sousTaches.stream()
                                .map(st -> new SousTacheDTO(st.getId(), st.getTitre(), st.isTermine()))
                                .toList();

                List<DocumentDTO> documentsDTO = documents.stream()
                                .map(d -> {
                                        String contenuBase64 = Base64.getEncoder().encodeToString(d.getContenu());
                                        String type = DocumentDTO.getFileType(d.getNom());
                                        int size = d.getContenu().length / 1024;
                                        return new DocumentDTO(
                                                        d.getId(),
                                                        d.getNom(),
                                                        d.getDescription(),
                                                        contenuBase64,
                                                        d.getDateCreation(),
                                                        size,
                                                        type);
                                })
                                .toList();

                List<CommentaireDTO> commentairesDTO = commentaires.stream()
                                .map(c -> new CommentaireDTO(
                                                c.getId(),
                                                "Anonyme",
                                                c.getContenu(),
                                                c.getDateCreation()))
                                .toList();

                Map<String, Object> tacheWrapper = new HashMap<>();
                tacheWrapper.put("tache", tacheDTO);
                tacheWrapper.put("sousTaches", sousTachesDTO);
                tacheWrapper.put("documents", documentsDTO);
                tacheWrapper.put("commentaires", commentairesDTO);

                Map<String, Object> response = new HashMap<>();
                response.put("tache", tacheWrapper);
                return ResponseEntity.ok(response);
        }

        //ajout ss tache a une tache specifique
        @PostMapping("/{id}/sous-tache")
        public ResponseEntity<SousTache> addSousTache(@PathVariable int id, @RequestBody Map<String, String> payload) {
                Tache tache = tacheRepository.findById(id).orElseThrow(() -> new RuntimeException("Tache non trouve"));
                SousTache sousTache = new SousTache();
                sousTache.setTitre(payload.get("titre"));
                sousTache.setTermine(false);
                sousTache.setDateCreation(LocalDate.now());
                sousTache.setTache(tache);
                SousTache saved = sousTacheRepository.save(sousTache);
                return ResponseEntity.ok(saved);
        }

        //modifier etat de ss tache si tache termine katweli non termine et contrairement
        @PutMapping("/sous-tache/{sousTacheId}")
        public ResponseEntity<SousTache> toggleSousTache(@PathVariable int sousTacheId) {
                SousTache sousTache = sousTacheRepository.findById(sousTacheId)
                                .orElseThrow(() -> new RuntimeException("Sous-tache non trouvee"));
                sousTache.setTermine(!sousTache.isTermine());
                SousTache updated = sousTacheRepository.save(sousTache);
                return ResponseEntity.ok(updated);
        }

        //ajout doc a une tache
        @PostMapping("/{id}/document")
        public ResponseEntity<Map<String, Object>> addDocument(
                        @PathVariable int id,
                        @RequestBody Map<String, String> payload) throws Exception {

                Tache tache = tacheRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("tache non trouvee"));

                // Récupérer le contenu en base64 depuis le payload
                String base64Content = payload.get("contenu");
                byte[] fileBytes = Base64.getDecoder().decode(base64Content);

                // Créer et sauvegarder le document
                Document document = new Document();
                document.setNom(payload.get("nom"));
                document.setDescription(payload.get("description"));
                document.setContenu(fileBytes); // Stocke les bytes bruts en BLOB
                document.setDateCreation(LocalDate.now());
                document.setTache(tache);

                Document saved = documentRepository.save(document);

                // Retourner une réponse avec le DTO
                DocumentDTO documentDTO = new DocumentDTO(
                                saved.getId(),
                                saved.getNom(),
                                saved.getDescription(),
                                base64Content, // On renvoie le meme contenu en base64
                                saved.getDateCreation(),
                                base64Content.length() / 1024,
                                DocumentDTO.getFileType(saved.getNom()));

                Map<String, Object> response = new HashMap<>();
                response.put("document", documentDTO);
                response.put("message", "Document ajoute avec succes !");

                return ResponseEntity.ok(response);
        }

        //ajout commentaire l tache
        @PostMapping("/{id}/commentaire")
        public ResponseEntity<Commentaire> addCommentaire(@PathVariable int id,
                        @RequestBody Map<String, String> payload) {
                Tache tache = tacheRepository.findById(id).orElseThrow(() -> new RuntimeException("tache non trouvee"));
                Membre membre = membreRepository.findById(Integer.parseInt(payload.get("membreID")))
                                .orElseThrow(() -> new RuntimeException("Membre non trouvee"));
                Commentaire commentaire = new Commentaire();
                commentaire.setMembre(membre);
                commentaire.setContenu(payload.get("contenu"));
                commentaire.setDateCreation(LocalDate.now());
                commentaire.setTache(tache);
                Commentaire saved = commentaireRepository.save(commentaire);
                return ResponseEntity.ok(saved);
        }

        //ila tra chi bloquage  fchi tache ajout dyalo et envoie notif l responsable du projet
        @PostMapping("/{id}/blocage")
        public ResponseEntity<Map<String, Object>> addBlocage(@PathVariable int id,
                        @RequestBody Map<String, String> payload) {
                Tache tache = tacheRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("tache non trouvee"));

                Blocage blocage = new Blocage();
                blocage.setDescription(payload.get("description"));
                blocage.setDateSignalement(LocalDate.now());
                blocage.setStatut("Signale");
                blocage.setTache(tache);
                Blocage savedBlocage = blocageRepository.save(blocage);

                Projet projet = tache.getProjet();
                if (projet == null) {
                        throw new RuntimeException("Projet non trouve pour cette tache");
                }

                Membre responsable = membreRepository.findByProjetIdAndRole(projet.getId(), "Responsable");
                if (responsable == null) {
                        throw new RuntimeException("Responsable du projet non trouve");
                }

                Notification notification = new Notification();
                notification.setContenu("Nouveau blocage pour la tache : " + tache.getTitre() + " ("
                                + payload.get("description") + ")");
                notification.setDateEnvoie(new Date());
                notification.setEstLue(false);
                notification.setMembre(responsable);
                notification.setProjet(projet);
                notificationRepository.save(notification);

                BlocageDTO blocageDTO = new BlocageDTO(
                                savedBlocage.getId(),
                                savedBlocage.getDescription(),
                                savedBlocage.getDateSignalement(),
                                savedBlocage.getStatut(),
                                savedBlocage.getDateResolution());

                Map<String, Object> response = new HashMap<>();
                response.put("blocage", blocageDTO);
                response.put("message", "Blocage ajoute avec succès et notification envoyee au responsable !");

                return ResponseEntity.ok(response);
        }

        //ajout du depense l tache et mise a jour l budget consomme
        @PostMapping("/{id}/depense")
        public ResponseEntity<Map<String, Object>> addDepense(@PathVariable int id,
                        @RequestBody Map<String, Integer> payload) {

                Tache tache = tacheRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("tache non trouvee"));

                int montant = payload.get("montant");
                tache.setDepense(tache.getDepense() + montant);
                Tache updatedTache = tacheRepository.save(tache);

                Projet projet = tache.getProjet();
                if (projet == null) {
                        throw new RuntimeException("Projet non trouve pour cette tache");
                }
                projet.setBudgetConsomme(projet.getBudgetConsomme() + montant);
                projetRepository.save(projet);

                Membre responsable = membreRepository.findByProjetIdAndRole(projet.getId(), "Responsable");
                if (responsable == null) {
                        throw new RuntimeException("Responsable du projet non trouve");
                }

                Notification notification = new Notification();
                notification.setContenu("Nouvelle depense pour la tache : " + tache.getTitre() + " ("
                                + payload.get("montant") + ")");
                notification.setDateEnvoie(new Date());
                notification.setEstLue(false);
                notification.setMembre(responsable);
                notification.setProjet(projet);
                notificationRepository.save(notification);

                Map<String, Object> response = new HashMap<>();
                response.put("tache", updatedTache);
                response.put("projet", projet);
                response.put("message", "Depense ajoute avec succes et budget du projet mis à jour !");

                return ResponseEntity.ok(response);
        }

        //supprime dune tache
        @DeleteMapping("/delete/{id}")
        public ResponseEntity<?> deleteTache(@PathVariable Integer id) {

                Optional<Tache> tache = tacheRepository.findById(id);

                if (tache.isEmpty()) {
                        return ResponseEntity
                                        .status(HttpStatus.NOT_FOUND)
                                        .body("tache introuvable");
                }

                tacheRepository.delete(tache.get());

                return ResponseEntity.ok("tache supprimee avec succes");
        }
}