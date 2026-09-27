package com.example.rescueapp.repository;

import com.example.rescueapp.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorRepo extends JpaRepository<Sensor, Long> {
}
