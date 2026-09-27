package com.example.rescueapp.repository;

import com.example.rescueapp.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlertRepo extends JpaRepository<Alert, Long> {

    List<Alert> findTop5ByActiveFalseOrderByCreatedAtDesc();

    Optional<Alert> findTopByActiveTrue();

}
