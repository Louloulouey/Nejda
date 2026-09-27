package org.luke.nejda.app.home.right.iot;

import javafx.scene.text.FontWeight;
import org.json.JSONObject;
import org.luke.gui.controls.Font;
import org.luke.gui.controls.alert.AbstractOverlay;
import org.luke.gui.controls.input.combo.KeyedTextItem;
import org.luke.gui.controls.label.keyed.Label;
import org.luke.gui.window.Window;
import org.luke.model.Alert;
import org.luke.model.AlertRequest;
import org.luke.model.Sensor;
import org.luke.nejda.app.home.right.emergency.CreateAlert;
import org.luke.nejda.ui.ComboVal;

public class CreateSensor extends AbstractOverlay {
    private final ComboVal location;

    private Sensor sensor;

    public CreateSensor(Window window) {
        super(window.getLoadedPage());

        Label head = new Label(window, "Add/Modify Sensor");
        head.setFont(new Font(20, FontWeight.BOLD));

        location = new ComboVal(window, "Sensor Location");

        for(int i = 1; i <= 4; i++) {
            location.getField().addItem(new KeyedTextItem(location.getField().getPopup(), "amphi_0" + i));
        }

        done.setAction(() -> {
            if(sensor == null) {
                this.sensor = new Sensor(new JSONObject());
            }
            this.sensor.setLocation(location.getValue());
            hide();
        });


        top.getChildren().add(head);
        center.getChildren().addAll(location);

        setOnCancel(() -> {
            sensor = null;
        });

        applyStyle(window.getStyl());
    }

    public CreateSensor init(Sensor sensor) {
        location.setValue(sensor.getLocation());
        this.sensor = sensor;
        return this;
    }



    public Sensor showForResult() {
        super.showAndWait();
        return sensor;
    }
} 