package com.example.demo.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.models.Rapport;
public interface RapportRepository extends JpaRepository<Rapport, Integer> {
    
}
