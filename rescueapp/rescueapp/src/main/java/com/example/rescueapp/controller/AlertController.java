package com.example.rescueapp.controller;

import com.example.rescueapp.model.Alert;
import com.example.rescueapp.model.AlertRequest;
import com.example.rescueapp.model.AlertRequestAttachment;
import com.example.rescueapp.model.User;
import com.example.rescueapp.repository.AlertRepo;
import com.example.rescueapp.repository.AlertRequestAttachmentRepo;
import com.example.rescueapp.repository.AlertRequestRepo;
import com.example.rescueapp.repository.UserRepo;
import com.example.rescueapp.service.NotificationManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/alert")
public class AlertController {

    @Autowired
    UserRepo userRepo;


    @Autowired
    AlertRepo alertRepo;

    @Autowired
    AlertRequestRepo alertRequestRepo;

    @Autowired
    AlertRequestAttachmentRepo alertRequestAttachmentRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/add")
    public Alert add(@RequestBody Alert body) {
        List<String> tokens = userRepo.findAll().stream().map(User::getFirebaseToken)
                .filter(Objects::nonNull).toList();
        NotificationManager.sendAlert(convertToJson(body), tokens);
        return alertRepo.save(body);
    }

    @PostMapping("/getRecent")
    public List<Alert> getRecent() {
        return alertRepo.findTop5ByActiveFalseOrderByCreatedAtDesc();
    }

    @PostMapping("/getActive")
    public Alert getActive() {
        return alertRepo.findTopByActiveTrue().orElse(null);
    }

    @PostMapping("/getAll")
    public List<Alert> getAll() {
        return alertRepo.findAll();
    }

    @PostMapping("/requestAlert")
    public AlertRequest requestAlert(@RequestBody AlertRequest alertRequest) {
        return alertRequestRepo.save(alertRequest);
    }

    @PostMapping("/treat")
    public AlertRequest treat(@RequestBody AlertRequest alertRequest) {
        alertRequest.setTreated(true);
        return alertRequestRepo.save(alertRequest);
    }

    @PostMapping("/end")
    public Alert end(@RequestBody Alert alert) {
        List<String> tokens = userRepo.findAll().stream().map(User::getFirebaseToken)
                .filter(Objects::nonNull).toList();
        NotificationManager.stopAlert(convertToJson(alert), tokens);
        alert.setActive(false);
        return alertRepo.save(alert);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody Alert alert) {
        alertRepo.delete(alert);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/getAlertRequests")
    public List<AlertRequest> getAlertRequests() {
        return alertRequestRepo.findAllByTreatedFalse();
    }

    @PostMapping("/saveRequestAttachment")
    public AlertRequestAttachment saveRequestAttachment(@RequestBody AlertRequestAttachment requestAttachment) {
        return alertRequestAttachmentRepo.save(requestAttachment);
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
