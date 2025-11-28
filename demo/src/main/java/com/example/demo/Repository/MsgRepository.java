package com.example.demo.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.models.Message;
public interface MsgRepository extends JpaRepository<Message, Integer> {
    List<Message> findByConversationId(int conversationId);
    List<Message> findByProjetId(int projetId);
}
