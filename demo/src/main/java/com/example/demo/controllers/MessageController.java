package com.example.demo.controllers;

import com.example.demo.models.Message;
import com.example.demo.models.Membre;
import com.example.demo.models.Projet;
import com.example.demo.repositories.MessageRepository;
import com.example.demo.repositories.MembreRepository;
import com.example.demo.repositories.ProjetRepository;
import com.example.demo.hooks.MessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/messages")
@CrossOrigin(origins = "*") // autorise les requêtes depuis Swing
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MembreRepository membreRepository;

    @Autowired
    private ProjetRepository projetRepository;

    @GetMapping("/projet/{projetId}")
    public List<Message> getMessagesByProjet(@PathVariable int projetId) {
        return messageRepository.findByProjetId(projetId);
    }

    @PostMapping
    public Message sendMessage(@RequestBody MessageDTO dto) {
        Optional<Projet> projetOpt = projetRepository.findById(dto.projetId);
        Optional<Membre> membreOpt = membreRepository.findById(dto.membreId);

        if (projetOpt.isEmpty() || membreOpt.isEmpty()) {
            throw new RuntimeException("Projet ou membre non trouvé");
        }

        Message message = new Message();
        message.setContenu(dto.contenu);
        message.setDateEnvoi(new Date());
        message.setEstLu(dto.estLu);
        message.setProjet(projetOpt.get());
        message.setMembre(membreOpt.get());

        return messageRepository.save(message);
    }
}
