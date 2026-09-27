package org.luke.model;


import org.json.JSONArray;
import org.json.JSONObject;
import org.luke.gui.exception.ErrorHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Thread extends Bean {
    private String title;

    private Boolean restricted;
    private Long id;
    private LocalDateTime createdAt;
    private List<Message> messages;

    public Thread(JSONObject data) {
        super(data);
    }

    @Override
    public JSONObject toJson() {
        JSONObject data = new JSONObject();
        try {
            data.put("id", id);
            data.put("restricted", restricted);
            data.put("title", title);
            data.put("createAt", createdAt);
            
            if (messages != null && !messages.isEmpty()) {
                JSONArray messagesArray = new JSONArray();
                for (Message message : messages) {
                    messagesArray.put(message.toJson());
                }
                data.put("messages", messagesArray);
            }
        } catch (Exception e) {
            ErrorHandler.handle(e, "serialize thread");
        }
        return data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getRestricted() {
        return restricted != null && restricted;
    }

    public void setRestricted(Boolean restricted) {
        this.restricted = restricted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public List<Message> getMessages() {
        return messages;
    }
    
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}