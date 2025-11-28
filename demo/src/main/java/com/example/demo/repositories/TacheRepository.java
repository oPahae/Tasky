package com.example.demo.repositories;

import com.example.demo.models.Tache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TacheRepository extends JpaRepository<Tache, Integer> {
    Optional<Tache> findById(int id);
}