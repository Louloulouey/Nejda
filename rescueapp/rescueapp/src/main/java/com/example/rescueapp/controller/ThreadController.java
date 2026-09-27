package com.example.rescueapp.controller;

import com.example.rescueapp.model.Message;
import com.example.rescueapp.model.Thread;
import com.example.rescueapp.repository.MessageRepository;
import com.example.rescueapp.repository.ThreadRepository;
import com.example.rescueapp.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/threads")
@CrossOrigin(origins = "*")
public class ThreadController {

    @Autowired
    private ThreadRepository threadRepository;
    
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepo userRepo;

    @PostMapping("/all")
    public List<Map<String, Object>> getAllThreads(
            @RequestParam(required = false, defaultValue = "0") int recentMessagesCount) {
        
        List<Thread> threads = threadRepository.findAll();
        
        return threads.stream().map(thread -> {
            Map<String, Object> threadMap = new HashMap<>();
            threadMap.put("id", thread.getId());
            threadMap.put("title", thread.getTitle());
            threadMap.put("restricted", thread.getRestricted());
            threadMap.put("createdAt", thread.getCreatedAt());
            
            // Get recent messages if requested
            if (recentMessagesCount > 0) {
                List<Message> recentMessages = messageRepository.findByThreadIdOrderByCreatedAtDesc(thread.getId())
                        .stream()
                        .limit(recentMessagesCount)
                        .collect(Collectors.toList());

                threadMap.put("messages", recentMessages);
            }
            
            return threadMap;
        }).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Thread> getThreadById(@PathVariable Long id) {
        return threadRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public Thread createThread(@RequestBody Thread thread) {
        return threadRepository.save(thread);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Thread> updateThread(@PathVariable Long id, @RequestBody Thread threadDetails) {
        return threadRepository.findById(id)
                .map(thread -> {
                    thread.setTitle(threadDetails.getTitle());
                    Thread updatedThread = threadRepository.save(thread);
                    return ResponseEntity.ok(updatedThread);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteThread(@PathVariable Long id) {
        return threadRepository.findById(id)
                .map(thread -> {
                    threadRepository.delete(thread);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}