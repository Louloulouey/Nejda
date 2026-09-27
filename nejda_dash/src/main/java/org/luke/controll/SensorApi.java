package org.luke.controll;

import org.luke.controll.abs.Api;
import org.luke.controll.abs.BodyParam;
import org.luke.model.Sensor;
import org.luke.model.SensorData;

import java.util.List;
import java.util.function.Consumer;

public class SensorApi extends Api {
    private static final String GET_SENSOR_DATA = "/sensor/getSensorHistory";
    private static final String GET_ALL = "/sensor/getAll";
    private static final String CREATE = "/sensor/addSensor";
    private static final String UPDATE = "/sensor/update";
    private static final String DELETE = "/sensor/delete";

    public static void getSensorHistory(Integer sensor, Consumer<List<SensorData>> onResponse) {
        sendRequest(GET_SENSOR_DATA, result -> {
                    onResponse.accept(parseList(result, SensorData.class));
                },
                new BodyParam("id", sensor));
    }

    public static void getAll(Consumer<List<Sensor>> onResponse) {
        sendRequest(GET_ALL, result -> {
            onResponse.accept(parseList(result, Sensor.class));
        });
    }

    public static void createSensor(Sensor sensor, Consumer<Sensor> onResponse) {
        sendRequest(CREATE, result -> {
            onResponse.accept(new Sensor(result));
        }, sensor);
    }

    public static void updateSensor(Sensor sensor, Consumer<Sensor> onResponse) {
        sendRequest(UPDATE, result -> {
            onResponse.accept(new Sensor(result));
        }, sensor);
    }

    public static void deleteSensor(Sensor sensor, Consumer<Boolean> onResponse) {
        sendRequest(DELETE, result -> {
            onResponse.accept(true);
        }, sensor);
    }
}
