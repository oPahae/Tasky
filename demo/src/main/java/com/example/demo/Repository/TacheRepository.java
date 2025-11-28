package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.models.Tache;

public interface TacheRepository extends JpaRepository<Tache, Integer> {


    
}
