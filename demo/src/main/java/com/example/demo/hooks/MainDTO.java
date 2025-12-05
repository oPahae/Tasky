package com.example.demo.hooks;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class MainDTO {
    public ProjetDTO projet;
    public List<MembreDTO> membres;
    public List<TacheDTO> taches;
    public List<DocumentDTO> documents;
    public List<BlocageDTO> blocages;
    public List<MessageDTO> messages;
    public List<NotificationDTO> notifications;

    public MainDTO() {
    }

    public MainDTO(ProjetDTO projet, List<MembreDTO> membres, List<TacheDTO> taches,
            List<DocumentDTO> documents,
            List<BlocageDTO> blocages, List<MessageDTO> messages,
            List<NotificationDTO> notifications) {
        this.projet = projet;
        this.membres = membres;
        this.taches = taches;
        this.documents = documents;
        this.blocages = blocages;
        this.messages = messages;
        this.notifications = notifications;
    }
}