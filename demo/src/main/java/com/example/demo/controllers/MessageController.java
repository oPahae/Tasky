package com.example.demo.controllers;

import com.example.demo.hooks.MessageDTO;
import com.example.demo.models.Message;
import com.example.demo.models.Membre;
import com.example.demo.models.Projet;
import com.example.demo.repositories.MessageRepository;
import com.example.demo.repositories.MembreRepository;
import com.example.demo.repositories.ProjetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MembreRepository membreRepository;

    @Autowired
    private ProjetRepository projetRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // REST : historique avec informations complètes du membre
    @GetMapping("/api/messages/projet/{projetId}")
    @ResponseBody
    public List<MessageDTO> getMessagesByProjet(@PathVariable int projetId) {
        return messageRepository.findByProjetId(projetId)
                .stream()
                .map(m -> {
                    MessageDTO dto = new MessageDTO();
                    dto.id = m.getId();
                    dto.contenu = m.getContenu();
                    dto.dateEnvoi = m.getDateEnvoi();
                    dto.estLu = m.isEstLu();
                    dto.projetId = m.getProjet().getId();
                    dto.membreId = m.getMembre().getId();

                    Membre membre = m.getMembre();
                    dto.prenomMembre = membre.getPrenom() != null ? membre.getPrenom() : "Inconnu";
                    dto.nomMembre = membre.getNom() != null ? membre.getNom() : "";
                    dto.typeMembre = membre.getRole() != null ? membre.getRole() : "Membre";

                    return dto;
                })
                .toList();
    }

    // WEBSOCKET : envoyer message avec informations du membre
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(MessageDTO dto) {
        System.out.println("📩 Message reçu via WebSocket:");
        System.out.println("   - Contenu: " + dto.contenu);
        System.out.println("   - MembreID: " + dto.membreId);
        System.out.println("   - ProjetID: " + dto.projetId);

        Projet projet = projetRepository.findById(dto.projetId);
        Optional<Membre> membreOpt = membreRepository.findById(dto.membreId);

        if (projet == null || membreOpt.isEmpty()) {
            System.err.println("Projet ou Membre introuvable");
            return;
        }

        Membre membre = membreOpt.get();

        // Sauvegarder le message
        Message message = new Message();
        message.setContenu(dto.contenu);
        message.setDateEnvoi(new Date());
        message.setEstLu(false);
        message.setProjet(projet);
        message.setMembre(membre);

        Message saved = messageRepository.save(message);
        System.out.println("✅ Message sauvegardé avec ID: " + saved.getId());

        // 🔹 IMPORTANT : Créer la réponse avec TOUTES les infos du membre
        MessageDTO response = new MessageDTO();
        response.id = saved.getId();
        response.contenu = saved.getContenu();
        response.dateEnvoi = saved.getDateEnvoi();
        response.estLu = saved.isEstLu();
        response.projetId = projet.getId();
        response.membreId = membre.getId();
        response.prenomMembre = membre.getPrenom() != null ? membre.getPrenom() : "Inconnu";
        response.nomMembre = membre.getNom() != null ? membre.getNom() : "";
        response.typeMembre = membre.getType() != null ? membre.getType() : "NORMAL";

        System.out.println("📤 Broadcast message vers /topic/projet/" + projet.getId());
        System.out.println("   - Membre: " + response.prenomMembre + " " + response.nomMembre);

        // Diffuser à tous les clients connectés
        messagingTemplate.convertAndSend(
                "/topic/projet/" + projet.getId(),
                response);

        System.out.println("✅ Message broadcasté avec succès");
    }
}