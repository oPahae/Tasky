package com.example.demo.repositories;

import com.example.demo.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByProjetId(int projetId);
    List<Notification> findByMembreIdAndProjetId(int membreID, int projetID);
}