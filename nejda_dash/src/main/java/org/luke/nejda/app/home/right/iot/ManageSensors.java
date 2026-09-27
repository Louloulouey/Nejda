package org.luke.nejda.app.home.right.iot;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import org.luke.controll.SensorApi;
import org.luke.gui.controls.image.ColoredIcon;
import org.luke.gui.controls.space.FixedHSpace;
import org.luke.gui.style.Style;
import org.luke.model.Sensor;
import org.luke.nejda.app.home.Home;
import org.luke.nejda.app.home.right.HomeContent;
import org.luke.nejda.ui.Button;

public class ManageSensors extends HomeContent {
    private final VBox sensors;

    public ManageSensors(Home home) {
        super(home);

        Button add = new Button(home.getWindow(), "Create", 160);
        add.add(new ColoredIcon(home.getWindow(), "add", 24, 18, Style::getTextOnAccent), new FixedHSpace(10));
        add.setAction(() -> {
            Sensor sensor = new CreateSensor(home.getWindow()).showForResult();
            if (sensor == null) return;
            add.startLoading();
            SensorApi.createSensor(sensor, resp -> {
                add.stopLoading();
                fetch();
            });
        });

        sensors = new VBox();
        sensors.setPadding(new Insets(10));
        sensors.setSpacing(10);

        fetch();

        addHeader(home.getWindow(), "Manage Sensors", new ColoredIcon(home.getWindow(),
                "refresh", 24, 20, Style::getTextNormal)
                .setOnAction(this::fetch), add);

        getChildren().add(sensors);

        applyStyle(home.getWindow().getStyl());
    }

    public void fetch() {
        SensorApi.getAll(sensorList -> {
            sensors.getChildren().clear();
            sensorList = sensorList.reversed();
            sensorList.forEach(sensor -> {
                this.sensors.getChildren().add(new SensorItem(getHome().getWindow(), sensor, this::fetch));
            });
        });
    }

    @Override
    public void setup() {
        fetch();
    }
}
