package org.luke.model;

import org.json.JSONObject;

public class Sensor extends Bean {

    private Long id;

    private String location;

    public Sensor(JSONObject data) {
        super(data);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) { this.id = id; }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "id=" + id +
                ", location=" + location +
                '}';
    }
}
