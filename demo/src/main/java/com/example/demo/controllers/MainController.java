package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.hooks.BlocageDTO;
import com.example.demo.hooks.DocumentDTO;
import com.example.demo.hooks.MainDTO;
import com.example.demo.hooks.MembreDTO;
import com.example.demo.hooks.MessageDTO;
import com.example.demo.hooks.NotificationDTO;
import com.example.demo.hooks.ProjetDTO;
import com.example.demo.hooks.SousTacheDTO;
import com.example.demo.hooks.TacheDTO;
import com.example.demo.models.Blocage;
import com.example.demo.models.Projet;
import com.example.demo.models.SousTache;
import com.example.demo.models.Tache;
import com.example.demo.repositories.BlocageRepository;
import com.example.demo.repositories.DocumentRepository;
import com.example.demo.repositories.MembreRepository;
import com.example.demo.repositories.MessageRepository;
import com.example.demo.repositories.NotificationRepository;
import com.example.demo.repositories.ProjetRepository;
import com.example.demo.repositories.SousTacheRepository;
import com.example.demo.repositories.TacheRepository;

@RestController
@RequestMapping("/api")
public class MainController {

    @Autowired
    private ProjetRepository projetRepository;

    @Autowired
    private MembreRepository membreRepository;

    @Autowired
    private TacheRepository tacheRepository;

    @Autowired
    private SousTacheRepository sousTacheRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private BlocageRepository blocageRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping("/all/{projetID}")
    public MainDTO getAllProjetInfo(@PathVariable int projetID) {
        Projet projet = projetRepository.findById(projetID);
        if (projet == null) {
            return null;
        }

        // convertir Projet en ProjetDTO
        ProjetDTO projetDTO = new ProjetDTO(
            projet.getId(),
            projet.getNom(),
            projet.getDescription(),
            projet.getDeadline(),
            projet.getBudget(),
            projet.getCode(),
            projet.getDateDebut(),
            projet.getBudgetConsomme(),
            projet.getStatut()
        );

        // recuperer et convertir les membres
        List<MembreDTO> membresDTO = projet.getMembres().stream()
            .map(m -> new MembreDTO(
                m.getNom(),
                m.getPrenom(),
                m.getEmail(),
                m.getDescription(),
                m.getRole(),
                m.getType(),
                m.getDateRejointe()
            ))
            .collect(Collectors.toList());

        // recuperer et convertir taches+ sstaches
        List<TacheDTO> tachesDTO = projet.getTaches().stream()
            .map(t -> {
                List<SousTache> sousTaches = sousTacheRepository.findByTacheId(t.getId());
                List<SousTacheDTO> sousTachesDTO = sousTaches.stream()
                    .map(st -> new SousTacheDTO(st.getId(), st.getTitre(), st.isTermine()))
                    .collect(Collectors.toList());
                return new TacheDTO(
                    t.getId(),
                    t.getTitre(),
                    t.getDescription(),
                    t.getDateLimite(),
                    t.getEtat(),
                    t.getDateCreation(),
                    t.getDateFin(),
                    0, 
                    sousTachesDTO
                );
            })
            .collect(Collectors.toList());

        // recuperer et convertir les docs
        List<DocumentDTO> documentsDTO = documentRepository.findAllByProjetId(projetID).stream()
            .map(d -> new DocumentDTO(
                d.getId(),
                d.getNom(),
                d.getDescription(),
                "",
                d.getDateCreation(),
                d.getContenu().length,
                DocumentDTO.getFileType(d.getNom())
            ))
            .collect(Collectors.toList());

        // recuperer et convertir les blocages
        List<BlocageDTO> blocagesDTO = new ArrayList<>();
        for (Tache tache : projet.getTaches()) {
            List<Blocage> blocages = blocageRepository.findByTacheId(tache.getId());
            blocagesDTO.addAll(
                blocages.stream()
                    .map(b -> new BlocageDTO(
                        b.getId(),
                        b.getDescription(),
                        b.getDateSignalement(),
                        b.getStatut(),
                        b.getDateResolution()
                    ))
                    .collect(Collectors.toList())
            );
        }

        // recuperer et convertir les messages
        List<MessageDTO> messagesDTO = messageRepository.findByProjetId(projetID).stream()
            .map(m -> new MessageDTO(
                m.getId(),
                m.getContenu(),
                m.getDateEnvoi(),
                m.isEstLu(),
                m.getProjet().getId(),
                m.getMembre().getId()
            ))
            .collect(Collectors.toList());

        // recuperer et convertir les notifications
        List<NotificationDTO> notificationsDTO = notificationRepository.findByProjetId(projetID).stream()
            .map(n -> new NotificationDTO(
                n.getId(),
                n.getContenu(),
                n.getDateEnvoie(),
                n.isEstLue()
            ))
            .collect(Collectors.toList());

        return new MainDTO(
            projetDTO,
            membresDTO,
            tachesDTO,
            documentsDTO,
            blocagesDTO,
            messagesDTO,
            notificationsDTO
        );
    }
}