package com.example.demo.controllers;

import com.example.demo.hooks.NotificationDTO;
import com.example.demo.models.Notification;
import com.example.demo.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notif")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    // Récupérer toutes les notifications pour un membre et un projet donné
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
            // Renvoie une Map avec une clé "notifications" pour correspondre au parsing du frontend
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