package com.example.demo.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.models.Projet;
public interface ProjetRepository extends  JpaRepository<Projet, Integer> {
    Projet findByName(String name);
    Projet findById(int id);
    Projet findByCode(String code);
}
