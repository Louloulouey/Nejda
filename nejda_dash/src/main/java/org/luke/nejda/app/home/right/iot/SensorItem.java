package org.luke.nejda.app.home.right.iot;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import org.luke.controll.SensorApi;
import org.luke.gui.controls.Font;
import org.luke.gui.controls.label.keyed.Label;
import org.luke.gui.controls.space.ExpandingHSpace;
import org.luke.gui.controls.space.Separator;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.window.Window;
import org.luke.model.Sensor;
import org.luke.nejda.ui.Button;

public class SensorItem extends HBox implements Styleable {
    private final Label locationLabel;
    private final Label idLabel;

    public SensorItem(Window window, Sensor sensor, Runnable afterDelete) {
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(10));
        setSpacing(10);

        locationLabel = new Label(window, "Location: " + sensor.getLocation(),
            new Font(Font.DEFAULT_FAMILY_MEDIUM, 14));

        idLabel = new Label(window, "ID: " + sensor.getId(),
            new Font(Font.DEFAULT_FAMILY_MEDIUM, 14));

        Button delete = new Button(window, "Delete");
        delete.setAction(() -> {
            delete.startLoading();
            SensorApi.deleteSensor(sensor, resp -> {
                delete.stopLoading();
                afterDelete.run();
            });
        });

        Button edit = new Button(window, "Edit");
        edit.setAction(() -> {
            Sensor edited = new CreateSensor(window)
                    .init(sensor)
                    .showForResult();
            if (edited == null) return;
            edit.startLoading();
            SensorApi.updateSensor(edited, resp -> {
                edit.stopLoading();
                afterDelete.run();
            });
        });

        Button viewData = new Button(window, "View Data");
        viewData.setAction(() -> {
            // Implement view data functionality
            // For example, open a popup showing sensor data history
        });

        getChildren().addAll(locationLabel, new Separator(window, Orientation.VERTICAL), idLabel, new ExpandingHSpace(), viewData, edit, delete);

        applyStyle(window.getStyl());
    }

    @Override
    public void applyStyle(Style style) {
        locationLabel.setFill(style.getTextNormal());
        idLabel.setFill(style.getTextNormal());
        setBackground(new Background(new BackgroundFill(
                style.getBackgroundSecondary(),
                new CornerRadii(5), null)));
    }

    @Override
    public void applyStyle(ObjectProperty<Style> style) {
        Styleable.bindStyle(this, style);
    }
} 