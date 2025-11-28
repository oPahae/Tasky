package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.models.SousTache;

public interface SousTacheRepository extends JpaRepository<SousTache, Integer> {}