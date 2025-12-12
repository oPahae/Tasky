package com.example.demo.controllers;

import com.example.demo.hooks.MessageDTO;
import com.example.demo.models.Message;
import com.example.demo.models.Membre;
import com.example.demo.models.Projet;
import com.example.demo.repositories.MessageRepository;
import com.example.demo.repositories.MembreRepository;
import com.example.demo.repositories.ProjetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ProjetRepository projetRepository;

    @Autowired
    private MembreRepository membreRepository;

    /**
     * Créer un nouveau message
     * POST /api/messages
     * Body JSON: {"contenu": "texte", "projetId": 1, "membreId": 1}
     */
    @PostMapping
    public ResponseEntity<MessageDTO> creerMessage(@RequestBody MessageDTO messageDTO) {
        try {
            Message message = new Message();
            message.setContenu(messageDTO.getContenu());
            message.setDateEnvoi(new Date());
            message.setEstLu(false);

            // Associer le projet si l'ID est fourni
            if (messageDTO.getProjetId() != null) {
                Projet projet = projetRepository.findById(messageDTO.getProjetId())
                        .orElseThrow(() -> new RuntimeException("Projet non trouvé"));
                message.setProjet(projet);
            }

            // Associer le membre si l'ID est fourni
            if (messageDTO.getMembreId() != null) {
                Membre membre = membreRepository.findById(messageDTO.getMembreId())
                        .orElseThrow(() -> new RuntimeException("Membre non trouvé"));
                message.setMembre(membre);
            }

            Message savedMessage = messageRepository.save(message);
            return new ResponseEntity<>(convertToDTO(savedMessage), HttpStatus.CREATED);
            
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Récupérer tous les messages
     * GET /api/messages
     */
    @GetMapping
    public ResponseEntity<List<MessageDTO>> obtenirTousLesMessages() {
        try {
            List<MessageDTO> messages = messageRepository.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(messages, HttpStatus.OK);
            
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les messages par projet
     * GET /api/messages/projet/{projetId}
     */
    @GetMapping("/projet/{projetId}")
    public ResponseEntity<List<MessageDTO>> obtenirMessagesParProjet(@PathVariable int projetId) {
        try {
            List<MessageDTO> messages = messageRepository.findByProjetId(projetId).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(messages, HttpStatus.OK);
            
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Marquer un message comme lu
     * PUT /api/messages/{id}/marquer-lu
     */
    @PutMapping("/{id}/marquer-lu")
    public ResponseEntity<MessageDTO> marquerCommeLu(@PathVariable int id) {
        try {
            Message message = messageRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Message non trouvé"));
            
            message.setEstLu(true);
            Message updatedMessage = messageRepository.save(message);
            return new ResponseEntity<>(convertToDTO(updatedMessage), HttpStatus.OK);
            
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Supprimer un message
     * DELETE /api/messages/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> supprimerMessage(@PathVariable int id) {
        try {
            if (!messageRepository.existsById(id)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            messageRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Méthode utilitaire pour convertir Message en MessageDTO
     */
    private MessageDTO convertToDTO(Message message) {
        return new MessageDTO(
                message.getId(),
                message.getContenu(),
                message.getDateEnvoi(),
                message.isEstLu(),
                message.getProjet() != null ? message.getProjet().getId() : null,
                message.getMembre() != null ? message.getMembre().getId() : null
        );
    }
}