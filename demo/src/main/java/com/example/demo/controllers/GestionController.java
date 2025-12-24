package com.example.demo.controllers;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.interfaces.PDFMonkeyService;
import com.example.demo.models.Document;
import com.example.demo.models.Notification;
import com.example.demo.models.Projet;
import com.example.demo.models.Tache;
import com.example.demo.repositories.DocumentRepository;
import com.example.demo.repositories.NotificationRepository;
import com.example.demo.repositories.ProjetRepository;
import com.example.demo.repositories.TacheRepository;

@RestController
@RequestMapping("/api/gestion")
@CrossOrigin(origins = "*")
public class GestionController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TacheRepository tacheRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private ProjetRepository projetRepository;

    @Autowired
    private PDFMonkeyService pdfMonkeyService;

    @GetMapping("/{projetId}")
    public ResponseEntity<Map<String, Object>> getGestionData(@PathVariable int projetId) {
        try {
            System.out.println("=== GET GESTION DATA ===");
            System.out.println("Projet ID: " + projetId);
            
            Map<String, Object> response = new HashMap<>();

            // Récupérer les informations du projet
            Optional<Projet> projetOpt = projetRepository.findById((Integer) projetId);
            if (projetOpt.isPresent()) {
                Projet projet = projetOpt.get();
                Map<String, Object> projetInfo = new HashMap<>();
                projetInfo.put("nom", projet.getNom());
                projetInfo.put("description", projet.getDescription());
                projetInfo.put("dateDebut", projet.getDateDebut() != null ? projet.getDateDebut().getTime() : null);
                projetInfo.put("dateFin", projet.getDateFin() != null ? projet.getDateFin().getTime() : null);
                projetInfo.put("budget", projet.getBudget());
                response.put("projet", projetInfo);
                System.out.println("Projet trouvé: " + projet.getNom());
            } else {
                System.out.println("Projet non trouvé!");
            }

            // Historique (notifications)
            List<Notification> notifications = notificationRepository.findByProjetId(projetId);
            System.out.println("Notifications trouvées: " + notifications.size());
            
            List<Map<String, Object>> historiqueDTO = notifications.stream()
                    .map(n -> {
                        Map<String, Object> notifMap = new HashMap<>();
                        notifMap.put("id", n.getId());
                        notifMap.put("contenu", n.getContenu());
                        notifMap.put("dateEnvoie", n.getDateEnvoie().getTime());
                        notifMap.put("estLue", n.isEstLue());
                        if (n.getMembre() != null) {
                            notifMap.put("membreNom", n.getMembre().getNom());
                            System.out.println("  - Notification: " + n.getContenu() + " par " + n.getMembre().getNom());
                        } else {
                            notifMap.put("membreNom", "Système");
                            System.out.println("  - Notification: " + n.getContenu() + " par Système");
                        }
                        return notifMap;
                    })
                    .sorted((n1, n2) -> ((Long) n2.get("dateEnvoie")).compareTo((Long) n1.get("dateEnvoie")))
                    .collect(Collectors.toList());

            // Facturation (tâches avec dépenses)
            List<Tache> taches = tacheRepository.findByProjetId(projetId);
            System.out.println("Tâches trouvées: " + taches.size());
            
            List<Map<String, Object>> facturation = taches.stream()
                    .filter(t -> t.getDepense() > 0)
                    .map(t -> {
                        Map<String, Object> expense = new HashMap<>();
                        expense.put("id", t.getId());
                        expense.put("titre", t.getTitre());
                        expense.put("depense", t.getDepense());
                        System.out.println("  - Tâche: " + t.getTitre() + " - " + t.getDepense() + " DHS");
                        return expense;
                    })
                    .collect(Collectors.toList());

            double totalDepenses = taches.stream()
                    .mapToDouble(Tache::getDepense)
                    .sum();
            System.out.println("Total dépenses: " + totalDepenses);

            // Documents
            List<Document> documents = documentRepository.findAllByProjetId(projetId);
            System.out.println("Documents trouvés: " + documents.size());
            
            List<Map<String, Object>> documentsDTO = documents.stream()
                    .map(d -> {
                        Map<String, Object> docMap = new HashMap<>();
                        docMap.put("id", d.getId());
                        docMap.put("nom", d.getNom());
                        docMap.put("description", d.getDescription());
                        docMap.put("contenuBase64", d.getContenu() != null ? 
                                Base64.getEncoder().encodeToString(d.getContenu()) : "");
                        if (d.getDateCreation() != null) {
                            docMap.put("dateCreation", 
                                    d.getDateCreation().atStartOfDay()
                                            .atZone(java.time.ZoneId.systemDefault())
                                            .toInstant()
                                            .toEpochMilli());
                        }
                        docMap.put("size", d.getContenu() != null ? d.getContenu().length : 0);
                        System.out.println("  - Document: " + d.getNom());
                        return docMap;
                    })
                    .sorted((d1, d2) -> ((Long) d2.get("dateCreation")).compareTo((Long) d1.get("dateCreation")))
                    .collect(Collectors.toList());

            response.put("historique", historiqueDTO);
            response.put("facturation", facturation);
            response.put("totalDepenses", totalDepenses);
            response.put("documents", documentsDTO);
            response.put("success", true);

            System.out.println("========================");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("ERREUR dans getGestionData: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Endpoint pour télécharger le rapport PDF
     */
    @GetMapping("/{projetId}/rapport/pdf")
    public ResponseEntity<byte[]> downloadRapportPDF(@PathVariable int projetId) {
        System.out.println("========================================");
        System.out.println("=== DÉBUT GÉNÉRATION RAPPORT PDF ===");
        System.out.println("Projet ID: " + projetId);
        System.out.println("========================================");
        
        try {
            // Récupérer les informations du projet
            Optional<Projet> projetOpt = projetRepository.findById((Integer)projetId);
            if (!projetOpt.isPresent()) {
                System.err.println("ERREUR: Projet non trouvé avec ID " + projetId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            
            Projet projet = projetOpt.get();
            System.out.println("Projet trouvé: " + projet.getNom());
            System.out.println("Budget: " + projet.getBudget() + " DHS");
            
            // Récupérer l'historique (notifications)
            System.out.println("\n--- RÉCUPÉRATION HISTORIQUE ---");
            List<Notification> notifications = notificationRepository.findByProjetId(projetId);
            System.out.println("Nombre de notifications: " + notifications.size());
            
            List<Map<String, Object>> historiqueDTO = notifications.stream()
                    .map(n -> {
                        Map<String, Object> notifMap = new HashMap<>();
                        notifMap.put("contenu", n.getContenu());
                        notifMap.put("dateEnvoie", n.getDateEnvoie().getTime());
                        if (n.getMembre() != null) {
                            notifMap.put("membreNom", n.getMembre().getNom());
                        } else {
                            notifMap.put("membreNom", "Système");
                        }
                        System.out.println("  ✓ " + notifMap.get("membreNom") + ": " + n.getContenu());
                        return notifMap;
                    })
                    .sorted((n1, n2) -> ((Long) n2.get("dateEnvoie")).compareTo((Long) n1.get("dateEnvoie")))
                    .collect(Collectors.toList());

            System.out.println("Historique formaté: " + historiqueDTO.size() + " événements");

            // Récupérer la facturation (tâches)
            System.out.println("\n--- RÉCUPÉRATION FACTURATION ---");
            List<Tache> taches = tacheRepository.findByProjetId(projetId);
            System.out.println("Nombre de tâches: " + taches.size());
            
            List<Map<String, Object>> facturation = taches.stream()
                .filter(t -> {
                    boolean hasExpense = t.getDepense() > 0;
                    if (hasExpense) {
                        System.out.println("  ✓ " + t.getTitre() + ": " + t.getDepense() + " DHS");
                    }
                    return hasExpense;
                })
                .map(t -> {
                    Map<String, Object> expense = new HashMap<>();
                    expense.put("tacheId", t.getId());
                    expense.put("tacheTitre", t.getTitre());
                    expense.put("tacheDescription", t.getDescription());
                    expense.put("etat", t.getEtat());
                    expense.put("depense", t.getDepense());

                    if (t.getDateFin() != null) {
                        expense.put("dateFin", 
                                t.getDateFin()
                                        .atStartOfDay()
                                        .atZone(java.time.ZoneId.systemDefault())
                                        .toInstant()
                                        .toEpochMilli());
                    }

                    return expense;
                })
                .collect(Collectors.toList());

            System.out.println("Facturation formatée: " + facturation.size() + " dépenses");

            double totalDepenses = taches.stream()
                    .mapToDouble(Tache::getDepense)
                    .sum();
            System.out.println("Total dépenses: " + totalDepenses + " DHS");

            // Récupérer les documents
            System.out.println("\n--- RÉCUPÉRATION DOCUMENTS ---");
            List<Document> documents = documentRepository.findAllByProjetId(projetId);
            System.out.println("Nombre de documents: " + documents.size());
            
            List<Map<String, Object>> documentsDTO = documents.stream()
                    .map(d -> {
                        Map<String, Object> docMap = new HashMap<>();
                        docMap.put("nom", d.getNom());
                        if (d.getDateCreation() != null) {
                            docMap.put("dateCreation", 
                                    d.getDateCreation().atStartOfDay()
                                            .atZone(java.time.ZoneId.systemDefault())
                                            .toInstant()
                                            .toEpochMilli());
                        }
                        docMap.put("size", d.getContenu() != null ? d.getContenu().length : 0);
                        System.out.println("  ✓ " + d.getNom() + " (" + docMap.get("size") + " bytes)");
                        return docMap;
                    })
                    .sorted((d1, d2) -> ((Long) d2.get("dateCreation")).compareTo((Long) d1.get("dateCreation")))
                    .collect(Collectors.toList());

            System.out.println("Documents formatés: " + documentsDTO.size() + " documents");

            // Vérification avant génération
            System.out.println("\n--- RÉSUMÉ AVANT GÉNÉRATION PDF ---");
            System.out.println("Projet: " + projet.getNom());
            System.out.println("Historique: " + historiqueDTO.size() + " événements");
            System.out.println("Facturation: " + facturation.size() + " dépenses");
            System.out.println("Documents: " + documentsDTO.size() + " documents");
            System.out.println("Total dépenses: " + totalDepenses + " DHS");
            System.out.println("Budget: " + projet.getBudget() + " DHS");
            System.out.println("-----------------------------------");

            // APPEL DU SERVICE PDFMONKEY
            System.out.println("\n🔄 Appel de PDFMonkeyService...\n");
            
            byte[] pdfContent = pdfMonkeyService.generateGestionReport(
                    projet,
                    historiqueDTO,
                    facturation,
                    documentsDTO,
                    totalDepenses
            );

            System.out.println("\n✅ PDF généré avec succès!");
            System.out.println("Taille du PDF: " + pdfContent.length + " bytes");

            // Préparer la réponse
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            String filename = "Rapport_" + projet.getNom().replaceAll("[^a-zA-Z0-9]", "_") + "_" + 
                    new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".pdf";
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            System.out.println("Nom du fichier: " + filename);
            System.out.println("========================================");
            System.out.println("=== FIN GÉNÉRATION RAPPORT PDF ===");
            System.out.println("========================================\n");

            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("\n❌ ERREUR LORS DE LA GÉNÉRATION DU PDF");
            System.err.println("Type: " + e.getClass().getName());
            System.err.println("Message: " + e.getMessage());
            System.err.println("\nStack trace:");
            e.printStackTrace();
            System.out.println("========================================\n");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}