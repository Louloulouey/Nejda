package com.sdidsa.nejda.controll;

import com.sdidsa.nejda.controll.abs.Api;
import com.sdidsa.nejda.controll.abs.BodyParam;
import com.sdidsa.nejda.model.SensorData;

import java.util.List;
import java.util.function.Consumer;

public class SensorApi extends Api {
    private static final String GET_SENSOR_DATA = "/sensor/getSensorHistory";

    public static void getSensorHistory(Integer sensor, Consumer<List<SensorData>> onResponse) {
        sendRequest(GET_SENSOR_DATA, result -> {
                    onResponse.accept(parseList(result, SensorData.class));
                },
                new BodyParam("id", sensor));
    }
}
