package com.sdidsa.nejda.model;
import org.json.JSONObject;

import java.time.LocalDateTime;

public class SensorData extends Bean {
    private Long id;

    private Sensor sensor;

    private Float value;

    private LocalDateTime createdAt;// = LocalDateTime.now();

    public SensorData(JSONObject data) {
        super(data);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public void setId(Integer id) { this.id = Long.valueOf(id); }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public void setSensor(JSONObject obj) {
        setSensor(new Sensor(obj));
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "SensorData{" +
                "id=" + id +
                ", sensor=" + sensor +
                ", value=" + value +
                ", createdAt=" + createdAt +
                '}';
    }
}