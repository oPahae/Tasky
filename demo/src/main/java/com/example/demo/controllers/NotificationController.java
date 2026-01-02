package com.example.demo.controllers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.hooks.NotificationDTO;
import com.example.demo.models.Notification;
import com.example.demo.repositories.NotificationRepository;

@RestController
@RequestMapping("/api/notif")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    // Recuperer toutes les notifications qui sont liees un membre et un projet donnex
    @GetMapping("/membre/{membreID}/projet/{projetID}")
    public ResponseEntity<Map<String, Object>> getNotificationsByMembreAndProjet(
            @PathVariable int membreID,
            @PathVariable int projetID) {
        try {
            List<Notification> notifications = notificationRepository.findByMembreIdAndProjetId(membreID, projetID);
            List<NotificationDTO> notificationDTOs = notifications.stream()
                    .map(notif -> new NotificationDTO(
                            notif.getId(),
                            notif.getContenu(),
                            notif.getDateEnvoie(),
                            notif.isEstLue()
                    ))
                    .collect(Collectors.toList());
            // Renvoie une Map avec une cle notifications pour correspondre au parsing du frontend
            return ResponseEntity.ok(Map.of("notifications", notificationDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Marquer une notification comme lue
    @PutMapping("/{id}/seen")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable int id) {
        try {
            Notification notification = notificationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Notification non trouvée"));
            notification.setEstLue(true);
            notificationRepository.save(notification);
            
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}