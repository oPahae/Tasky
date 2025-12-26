package com.example.demo.repositories;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.models.Membre;

public interface MembreRepository extends JpaRepository<Membre, Integer> {
    List<Membre> findByUser_Id(int userId);
    Membre findByProjetIdAndRole(int projetId, String role);
    List<Membre> findByProjetId(int projetId);
}