package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.models.Appel;

public interface AppelRepository extends JpaRepository<Appel, Integer> {
    
}
