package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.models.Commentaire;
public interface CmtRepository extends JpaRepository<Commentaire, Integer> {
    
}
