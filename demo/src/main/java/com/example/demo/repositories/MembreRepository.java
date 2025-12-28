package com.example.demo.repositories;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.demo.models.Membre;

public interface MembreRepository extends JpaRepository<Membre, Integer> {
    List<Membre> findByUser_Id(int userId);
    Membre findByProjetIdAndRole(int projetId, String role);
    List<Membre> findByProjetId(int projetId);
    
    // ✅ NOUVELLE MÉTHODE
    @Query("SELECT m FROM Membre m WHERE m.user.id = :userId AND m.projet.id = :projetId")
    Membre findByUserIdAndProjetId(@Param("userId") int userId, @Param("projetId") int projetId);
}