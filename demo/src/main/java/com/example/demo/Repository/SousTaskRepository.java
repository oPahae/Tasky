package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.models.SousTache;

public interface SousTaskRepository extends JpaRepository<SousTache, Integer> {

}
