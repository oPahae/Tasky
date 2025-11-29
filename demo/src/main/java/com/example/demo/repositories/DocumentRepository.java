package com.example.demo.repositories;
import com.example.demo.models.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {
    List<Document> findByTacheId(int tacheId);
}