package com.example.demo.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.models.Rapport;
public interface RapportRepository extends JpaRepository<Rapport, Integer> {
    
}
