package com.example.rescueapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "alert_request")
public class AlertRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "severity")
    private Integer severity;

    @Column(name = "type")
    private String type;

    @Column(name = "details")
    private String details;

    @Column(name = "treated")
    private Boolean treated = false;

    @JsonManagedReference
    @OneToMany(mappedBy = "alertRequest", cascade = CascadeType.ALL)
    private List<AlertRequestAttachment> attachments;

    @ManyToOne
    @JoinColumn(name = "reported_by", referencedColumnName = "user_id")
    private User reportedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSeverity() {
        return severity;
    }

    public void setSeverity(Integer severity) {
        this.severity = severity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Boolean getTreated() {
        return treated;
    }

    public void setTreated(Boolean treated) {
        this.treated = treated;
    }

    public User getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(User reportedBy) {
        this.reportedBy = reportedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<AlertRequestAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AlertRequestAttachment> attachments) {
        this.attachments = attachments;
    }
}
