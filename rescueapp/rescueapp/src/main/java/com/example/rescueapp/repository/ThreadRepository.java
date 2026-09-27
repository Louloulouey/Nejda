package com.example.rescueapp.repository;

import com.example.rescueapp.model.Thread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThreadRepository extends JpaRepository<Thread, Long> {
    // Add custom queries if needed
}