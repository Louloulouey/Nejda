package com.example.rescueapp.repository;

import com.example.rescueapp.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepo extends JpaRepository<Teacher, String> {
}
