package com.example.rescueapp.controller;

import com.example.rescueapp.model.User;
import com.example.rescueapp.repository.StudentRepo;
import com.example.rescueapp.repository.TeacherRepo;
import com.example.rescueapp.repository.UserRepo;
import com.example.rescueapp.requests.LoginRequest;
import com.example.rescueapp.service.NotificationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserRepo userRepo;

    @Autowired
    StudentRepo studentRepo;

    @Autowired
    TeacherRepo teacherRepo;

    @PostMapping("/login")
    public User login(@RequestBody LoginRequest request) {
        return userRepo.findByUserIdAndPassword(
                        request.getUserId(),
                        request.getPassword())
                .orElse(null);
    }

    @PostMapping("/addUser")
    public User addUser(@RequestBody User user) {
        return userRepo.save(user);
    }

    @PostMapping("/setPfp")
    public User setPfp(@RequestBody User user) {
        User old = userRepo.findById(user.getUserId()).orElse(user);
        old.setPfp(user.getPfp());
        return userRepo.save(old);
    }

    @PostMapping("/getUser")
    public User getUser(@RequestBody User user) {
        return userRepo.findById(user.getUserId()).orElse(null);
    }

    @PostMapping("/logout")
    public User logout(@RequestBody User user) {
        user.setFirebaseToken(null);
        userRepo.save(user);
        return user;
    }

    @PostMapping("/saveFirebaseToken")
    public User saveFirebaseToken(@RequestBody User user) {
        return userRepo.save(user);
    }
}
