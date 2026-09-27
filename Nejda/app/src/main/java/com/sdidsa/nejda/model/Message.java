package com.sdidsa.nejda.model;

import com.sdidsa.nejda.abs.utils.ErrorHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Message extends Bean {
    private Long id;
    private String text;
    private User sender;
    private Thread thread;
    private LocalDateTime createdAt;
    private List<MessageAttachment> attachments;

    public Message(JSONObject data) {
        super(data);
    }

    @Override
    public JSONObject toJson() {
        JSONObject data = new JSONObject();
        try {
            data.put("id", id);
            data.put("text", text);
            if (sender != null) {
                data.put("sender", sender.toJson());
            }
            if (thread != null) {
                data.put("thread", thread.toJson());
            }
            data.put("createdAt", createdAt);
            if (attachments != null && !attachments.isEmpty()) {
                JSONArray attachmentsArray = new JSONArray();
                for (MessageAttachment attachment : attachments) {
                    attachmentsArray.put(attachment.toJson());
                }
                data.put("attachments", attachmentsArray);
            }
        } catch (Exception e) {
            ErrorHandler.handle(e, "serialize message");
        }
        return data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<MessageAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<MessageAttachment> attachments) {
        this.attachments = attachments;
    }

    public void addAttachment(MessageAttachment attachment) {
        if (attachments == null) {
            attachments = new ArrayList<>();
        }
        attachments.add(attachment);
        attachment.setMessage(this);
    }

    public void removeAttachment(MessageAttachment attachment) {
        if (attachments != null) {
            attachments.remove(attachment);
            attachment.setMessage(null);
        }
    }
} 