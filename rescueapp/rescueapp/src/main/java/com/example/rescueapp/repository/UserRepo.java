package com.example.rescueapp.repository;

import com.example.rescueapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, String> {

    Optional<User> findByUserIdAndPassword(String userId, String password);

}
