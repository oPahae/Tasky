package com.example.demo.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.models.Notification;
public interface NotifRepository  extends JpaRepository<Notification, Integer> {
    
}
