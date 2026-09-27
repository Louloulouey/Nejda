package com.example.rescueapp.service;

import com.example.rescueapp.model.Sensor;
import com.example.rescueapp.model.SensorData;
import com.example.rescueapp.repository.SensorDataRepo;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

@Component
public class SerialReader {

    @Autowired
    private SensorDataRepo dataRepo;

    @Autowired
    private NotificationService analyser;

    @PostConstruct
    public void startReading() {
        new Thread(() -> {
            SerialPort port = SerialPort.getCommPort("/dev/ttyUSB0");
            port.setBaudRate(9600);
            if (port.openPort()) {
                port.addDataListener(new SerialPortDataListener() {
                    @Override
                    public int getListeningEvents() {
                        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
                    }

                    @Override
                    public void serialEvent(SerialPortEvent event) {
                        if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                            return;

                        byte[] buffer = new byte[port.bytesAvailable()];
                        int numRead = port.readBytes(buffer, buffer.length);

                        if (numRead > 0) {
                            String data = new String(buffer, 0, numRead, StandardCharsets.UTF_8).trim();
                            System.out.println(data);
                            String[] parts;
                            for(String line : data.split(";")) {
                                if(line.contains(":") && (parts = line.split(":")).length == 3) {
                                    SensorData dat = parseData(parts);

                                    dat = dataRepo.save(dat);

                                    SensorData finalDat = dat;
                                    new Thread(() -> {
                                        analyser.analyseSensorData(finalDat);
                                    }).start();
                                }
                            }

                        }
                    }
                });
            } else {
                System.err.println("Failed to open port.");
            }
        }).start();
    }

    private static SensorData parseData(String[] parts) {
        int sensorId = Integer.parseInt(parts[0].trim());
        String type = parts[1].trim();
        int value = Integer.parseInt(parts[2].trim());

        SensorData dat = new SensorData();
        Sensor sensor = new Sensor();
        sensor.setId((long) sensorId);

        dat.setSensor(sensor);
        dat.setType(type);
        dat.setValue((float) value);
        return dat;
    }
}
