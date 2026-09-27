package com.example.rescueapp.repository;

import com.example.rescueapp.model.Message;
import com.example.rescueapp.model.MessageAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageAttachmentRepo extends JpaRepository<MessageAttachment, Long> {
    List<MessageAttachment> findAllByMessage(Message message);
}
