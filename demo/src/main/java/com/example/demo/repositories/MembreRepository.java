package com.example.demo.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.models.Membre;
public interface MembreRepository extends JpaRepository<Membre, Integer> {}