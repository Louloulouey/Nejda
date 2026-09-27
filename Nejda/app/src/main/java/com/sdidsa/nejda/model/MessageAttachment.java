package com.sdidsa.nejda.model;

import com.sdidsa.nejda.abs.utils.ErrorHandler;

import org.json.JSONObject;

public class MessageAttachment extends Bean {
    private Long id;
    private String url;
    private String type;
    private Message message;

    public MessageAttachment(JSONObject data) {
        super(data);
    }

    public MessageAttachment(String url, String type) {
        super(new JSONObject());
        this.url = url;
        this.type = type;
    }

    @Override
    public JSONObject toJson() {
        JSONObject data = new JSONObject();
        try {
            data.put("id", id);
            data.put("url", url);
            data.put("type", type);
        } catch (Exception e) {
            ErrorHandler.handle(e, "serialize message attachment");
        }
        return data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
} 