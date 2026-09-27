package com.example.rescueapp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "alert_request_attachment")
public class AlertRequestAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "url")
    private String url;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "request_id", referencedColumnName = "id")
    private AlertRequest alertRequest;

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
