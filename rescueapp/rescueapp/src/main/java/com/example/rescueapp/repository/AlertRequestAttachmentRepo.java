package com.example.rescueapp.repository;

import com.example.rescueapp.model.AlertRequestAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRequestAttachmentRepo extends JpaRepository<AlertRequestAttachment, Long> {
}
