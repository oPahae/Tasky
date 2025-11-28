package com.example.demo.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.models.Appel;

public interface AppelRepository extends JpaRepository<Appel, Integer> {
    
}
