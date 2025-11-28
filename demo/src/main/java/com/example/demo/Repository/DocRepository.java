package com.example.demo.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.models.Document;

public interface DocRepository extends JpaRepository<Document, Integer> {

}
