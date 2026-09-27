package org.luke.model;


import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.List;

public class AlertRequest extends Bean {

    private Long id;

    private Integer severity;

    private String type;

    private String details;

    private Boolean treated = false;

    private List<AlertRequestAttachment> attachments;

    private User reportedBy;

    private LocalDateTime createdAt;

    public AlertRequest(JSONObject data) {
        super(data);
    }

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
