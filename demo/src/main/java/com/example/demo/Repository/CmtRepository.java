package com.example.demo.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.models.Commentaire;
public interface CmtRepository extends JpaRepository<Commentaire, Integer> {
    
}
