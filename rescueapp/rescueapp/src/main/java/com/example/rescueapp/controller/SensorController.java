package com.example.rescueapp.controller;

import com.example.rescueapp.model.Sensor;
import com.example.rescueapp.model.SensorData;
import com.example.rescueapp.model.User;
import com.example.rescueapp.repository.SensorDataRepo;
import com.example.rescueapp.repository.SensorRepo;
import com.example.rescueapp.repository.UserRepo;
import com.example.rescueapp.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/sensor")
@CrossOrigin(origins = "*")
public class SensorController {

    @Autowired
    private SensorDataRepo sensorDataRepo;

    @Autowired
    private SensorRepo sensorRepo;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepo userRepo;

    @PostMapping("/addSensorData")
    public ResponseEntity<Map<String, String>> addSensorData(@RequestBody SensorData alert) {
        alert = sensorDataRepo.save(alert);

        notificationService.analyseSensorData(alert);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Sensor active, value : " + alert.getValue());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/addSensor")
    public Sensor addSensor(@RequestBody Sensor sensor) {
        return sensorRepo.save(sensor);
    }

    @PostMapping("/getAll")
    public List<Sensor> getAll() {
        return sensorRepo.findAll();
    }

    @PostMapping("/update")
    public Sensor update(@RequestBody Sensor sensor) {
        return sensorRepo.save(sensor);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody Sensor sensor) {
        sensorRepo.delete(sensor);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/getSensorHistory")
    public List<SensorData> getSensorHistory(@RequestBody Sensor sensor) {
        return sensorDataRepo.findAllBySensor(sensor);
    }
}
