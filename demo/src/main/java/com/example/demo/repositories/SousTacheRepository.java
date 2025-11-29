package com.example.demo.repositories;
import com.example.demo.models.SousTache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface SousTacheRepository extends JpaRepository<SousTache, Integer> {
    List<SousTache> findByTacheId(int tacheId);
}