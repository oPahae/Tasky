package com.example.demo.controllers;

import com.example.demo.hooks.DocumentDTO;
import com.example.demo.hooks.NotificationDTO;
import com.example.demo.models.Document;
import com.example.demo.models.Notification;
import com.example.demo.models.Tache;
import com.example.demo.repositories.DocumentRepository;
import com.example.demo.repositories.NotificationRepository;
import com.example.demo.repositories.TacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

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

    @GetMapping("/{projetId}")
    public ResponseEntity<Map<String, Object>> getGestionData(@PathVariable int projetId) {
        try {
            Map<String, Object> response = new HashMap<>();

            List<Notification> notifications = notificationRepository.findByProjetId(projetId);
            List<Map<String, Object>> historiqueDTO = notifications.stream()
                    .map(n -> {
                        Map<String, Object> notifMap = new HashMap<>();
                        notifMap.put("id", n.getId());
                        notifMap.put("contenu", n.getContenu());
                        notifMap.put("dateEnvoie", n.getDateEnvoie().getTime());
                        notifMap.put("estLue", n.isEstLue());
                        if (n.getMembre() != null) {
                            notifMap.put("membreNom", n.getMembre().getNom());
                        }
                        return notifMap;
                    })
                    .sorted((n1, n2) -> ((Long) n2.get("dateEnvoie")).compareTo((Long) n1.get("dateEnvoie")))
                    .collect(Collectors.toList());

            List<Tache> taches = tacheRepository.findByProjetId(projetId);
            List<Map<String, Object>> facturation = taches.stream()
                    .filter(t -> t.getDepense() > 0)
                    .map(t -> {
                        Map<String, Object> expense = new HashMap<>();
                        expense.put("id", t.getId());
                        expense.put("titre", t.getTitre());
                        expense.put("depense", t.getDepense());
                        return expense;
                    })
                    .collect(Collectors.toList());

            double totalDepenses = taches.stream()
                    .mapToDouble(Tache::getDepense)
                    .sum();

            List<Document> documents = documentRepository.findAllByProjetId(projetId);
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
                        docMap.put("type", DocumentDTO.getFileType(d.getNom()));
                        return docMap;
                    })
                    .sorted((d1, d2) -> ((Long) d2.get("dateCreation")).compareTo((Long) d1.get("dateCreation")))
                    .collect(Collectors.toList());

            response.put("historique", historiqueDTO);
            response.put("facturation", facturation);
            response.put("totalDepenses", totalDepenses);
            response.put("documents", documentsDTO);
            response.put("success", true);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}