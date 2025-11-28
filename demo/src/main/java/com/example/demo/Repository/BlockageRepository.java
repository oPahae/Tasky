package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.Blockage;

public interface BlockageRepository extends JpaRepository<Blockage, Integer> {


    
}
