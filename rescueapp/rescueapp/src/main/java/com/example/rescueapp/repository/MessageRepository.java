package com.example.rescueapp.repository;

import com.example.rescueapp.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByThreadIdOrderByCreatedAtDesc(Long threadId);
    List<Message> findByThreadIdOrderByCreatedAtAsc(Long threadId);
}