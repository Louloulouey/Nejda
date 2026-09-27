package com.example.rescueapp.repository;

import com.example.rescueapp.model.AlertRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRequestRepo extends JpaRepository<AlertRequest, Long> {
    List<AlertRequest> findAllByTreatedFalse();
}
