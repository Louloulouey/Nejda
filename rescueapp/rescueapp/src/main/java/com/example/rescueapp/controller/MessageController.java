package com.example.rescueapp.controller;

import com.example.rescueapp.model.Message;
import com.example.rescueapp.model.MessageAttachment;
import com.example.rescueapp.model.User;
import com.example.rescueapp.repository.MessageAttachmentRepo;
import com.example.rescueapp.repository.MessageRepository;
import com.example.rescueapp.repository.ThreadRepository;
import com.example.rescueapp.repository.UserRepo;
import com.example.rescueapp.service.NotificationManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageAttachmentRepo messageAttachmentRepo;

    @Autowired
    private ThreadRepository threadRepository;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private final ObjectMapper objectMapper;

    public MessageController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @PostMapping("/all/{threadId}")
    public List<Message> getMessagesByThread(@PathVariable Long threadId) {
        List<Message> messages = messageRepository.findByThreadIdOrderByCreatedAtAsc(threadId);
        
        for (Message message : messages) {
            List<MessageAttachment> attachments = messageAttachmentRepo.findAllByMessage(message);
            message.setAttachments(attachments);
        }
        
        return messages;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable Long id) {
        return messageRepository.findById(id)
                .map(message -> {
                    // Fetch message attachments
                    List<MessageAttachment> attachments = messageAttachmentRepo.findAllByMessage(message);
                    message.setAttachments(attachments);
                    return ResponseEntity.ok(message);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    public String convertToJson(Object bean) {
        try {
            return objectMapper.writeValueAsString(bean);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/create/{threadId}")
    public ResponseEntity<Message> createMessage(@PathVariable Long threadId, @RequestBody Message message) {
        return threadRepository.findById(threadId)
                .map(thread -> {
                    message.setThread(thread);
                    
                    // Save message first
                    Message savedMessage = messageRepository.save(message);
                    
                    // Handle attachments if any
                    List<MessageAttachment> attachments = message.getAttachments();
                    System.out.println("Creating message with " + (attachments != null ? attachments.size() : 0) + " attachments");
                    
                    if (attachments != null && !attachments.isEmpty()) {
                        for (MessageAttachment attachment : attachments) {
                            attachment.setMessage(savedMessage);
                            MessageAttachment savedAttachment = messageAttachmentRepo.save(attachment);
                            System.out.println("Saved attachment: ID=" + savedAttachment.getId() + 
                                             ", URL=" + savedAttachment.getUrl() + 
                                             ", Type=" + savedAttachment.getType());
                        }
                    }

                    // Fetch the complete message with attachments
                    Message resp = messageRepository.findById(savedMessage.getId()).orElseThrow();
                    resp.setSender(userRepo.findById(savedMessage.getSender().getUserId()).orElseThrow());
                    resp.setThread(threadRepository.findById(savedMessage.getThread().getId()).orElseThrow());
                    
                    // Fetch attachments for the response
                    List<MessageAttachment> respAttachments = messageAttachmentRepo.findAllByMessage(resp);
                    System.out.println("Found " + respAttachments.size() + " attachments for response message");
                    resp.setAttachments(respAttachments);
                    
                    // Debug JSON output
                    String jsonOutput = convertToJson(resp);
                    System.out.println("Message with attachments: " + jsonOutput);
                    System.out.println("Contains 'attachments' field: " + jsonOutput.contains("attachments"));
                    
                    System.out.println("Thread ID: " + resp.getThread().getId());

                    // Send notifications
                    List<User> users = userRepo.findAll();
                    List<String> tokens = users.stream()
                            .map(User::getFirebaseToken)
                            .filter(Objects::nonNull).toList();
                    if (!tokens.isEmpty()) {
                        NotificationManager.sendMessage(convertToJson(resp), tokens);
                    }

                    return ResponseEntity.ok(resp);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Message> updateMessage(@PathVariable Long id, @RequestBody Message messageDetails) {
        return messageRepository.findById(id)
                .map(message -> {
                    message.setText(messageDetails.getText());
                    Message updatedMessage = messageRepository.save(message);
                    
                    // Fetch attachments for the response
                    List<MessageAttachment> attachments = messageAttachmentRepo.findAllByMessage(updatedMessage);
                    updatedMessage.setAttachments(attachments);
                    
                    return ResponseEntity.ok(updatedMessage);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long id) {
        return messageRepository.findById(id)
                .map(message -> {
                    messageRepository.delete(message);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}