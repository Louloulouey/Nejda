// === SERVICE ===
package com.example.rescueapp.service;

import com.example.rescueapp.model.Alert;
import com.example.rescueapp.model.Sensor;
import com.example.rescueapp.model.SensorData;
import com.example.rescueapp.model.User;
import com.example.rescueapp.repository.AlertRepo;
import com.example.rescueapp.repository.SensorRepo;
import com.example.rescueapp.repository.UserRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
public class NotificationService {
    private static final double CRITICAL_TEMP = 70;

    @Autowired
    UserRepo userRepo;

    @Autowired
    SensorRepo sensorRepo;

    @Autowired
    AlertRepo alertRepo;

    @Autowired
    private ObjectMapper objectMapper;

    private final HashMap<String, Alert> activeAlerts = new HashMap<>();

    public void analyseSensorData(SensorData data) {
        String key = data.getSensor().getId() + "_" + data.getType();
        Alert activeAlert = activeAlerts.get(key);

        if(data.getType().equalsIgnoreCase("temperature") &&
                data.getValue() >= CRITICAL_TEMP) {
            System.out.println("alerting temps !!");
            if(activeAlert == null) {
                System.out.println("activating alarm");
                Alert alert = new Alert();
                alert.setActive(true);
                Sensor sensor = sensorRepo.findById(data.getSensor().getId()).orElseThrow();
                alert.setLocation(sensor.getLocation());
                alert.setTitle("Fire in " + sensor.getLocation());
                alert.setBody("Automatically triggered alert cause by sensor data");
                alert.setSeverity(2);
                activeAlert = alertRepo.save(alert);

                List<String> tokens = userRepo.findAll().stream().map(User::getFirebaseToken)
                        .filter(Objects::nonNull).toList();
                NotificationManager.sendAlert(convertToJson(activeAlert), tokens);
            } else {
                System.out.println("alarm is active");
            }
        } else if(activeAlert != null) {
            activeAlert.setActive(false);
            activeAlert = alertRepo.save(activeAlert);
            System.out.println("deactivating alarm");

            List<String> tokens = userRepo.findAll().stream().map(User::getFirebaseToken)
                    .filter(Objects::nonNull).toList();
            NotificationManager.stopAlert(convertToJson(activeAlert), tokens);

            activeAlert = null;
        }

        activeAlerts.put(key, activeAlert);
    }

    public String convertToJson(Object bean) {
        try {
            return objectMapper.writeValueAsString(bean);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}