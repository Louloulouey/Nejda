package org.luke.model;

import org.json.JSONObject;

public class AlertRequestAttachment extends Bean {

    private Long id;

    private String url;

    private AlertRequest alertRequest;

    public AlertRequestAttachment(JSONObject data) {
        super(data);
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

    public AlertRequest getAlertRequest() {
        return alertRequest;
    }

    public void setAlertRequest(AlertRequest alertRequest) {
        this.alertRequest = alertRequest;
    }
}
