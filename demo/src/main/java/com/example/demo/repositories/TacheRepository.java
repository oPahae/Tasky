package com.example.demo.repositories;

import com.example.demo.models.Membre;
import com.example.demo.models.Tache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TacheRepository extends JpaRepository<Tache, Integer> {
    Optional<Tache> findById(int id);

    List<Tache> findByMembresId(int id);

    List<Tache> findByProjetId(int id);

    @Query("SELECT t FROM Tache t JOIN t.membres m WHERE m = :membre")
    List<Tache> findByMembresContaining(@Param("membre") Membre membre);
}