package com.sdidsa.nejda.model;

import org.json.JSONObject;

public class Sensor extends Bean {

    private Long id;

    private Integer floor;

    public Sensor(JSONObject data) {
        super(data);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) { this.id = id; }

    public int getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "id=" + id +
                ", floor=" + floor +
                '}';
    }
}
