package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.models.Notification;
public interface NotifRepository  extends JpaRepository<Notification, Integer> {
    
}
