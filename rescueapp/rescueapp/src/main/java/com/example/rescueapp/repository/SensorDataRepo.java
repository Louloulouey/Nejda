package com.example.rescueapp.repository;

import com.example.rescueapp.model.Sensor;
import com.example.rescueapp.model.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SensorDataRepo extends JpaRepository<SensorData, Long> {

    List<SensorData> findAllBySensor(Sensor sensor);

}
