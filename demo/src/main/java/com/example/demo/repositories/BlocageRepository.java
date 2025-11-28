package com.example.demo.repositories;
import com.example.demo.models.Blocage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlocageRepository extends JpaRepository<Blocage, Integer> {}