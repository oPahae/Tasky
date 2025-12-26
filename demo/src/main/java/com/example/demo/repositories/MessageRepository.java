package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    
    // Récupérer tous les messages d'un projet spécifique
    List<Message> findByProjetId(int projetId);
    
    // Récupérer les messages non lus d'un projet
    List<Message> findByProjetIdAndEstLuFalse(int projetId);
    
    // Récupérer les messages d'un membre dans un projet
    List<Message> findByProjetIdAndMembreId(int projetId, int membreId);
}