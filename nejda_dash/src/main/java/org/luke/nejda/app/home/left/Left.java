package org.luke.nejda.app.home.left;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import org.luke.gui.controls.space.FixedVSpace;
import org.luke.gui.controls.space.Separator;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.window.Window;
import org.luke.nejda.app.home.Home;
import org.luke.nejda.app.home.right.emergency.alerts.ManageAlerts;
import org.luke.nejda.app.home.right.emergency.requests.UserSubmitted;
import org.luke.nejda.app.home.right.iot.ManageSensors;

import java.util.ArrayList;

public class Left extends VBox implements Styleable {
    private final ArrayList<Section> sections;

    public Left(Home home) {
        setPadding(new Insets(10, 5, 10, 5));
        setMinWidth(218);
        setAlignment(Pos.TOP_CENTER);

        sections = new ArrayList<>();

        Section alerts = new Section(home, "Emergencies", "danger", true);
        alerts.addItem(new SectionItem(home, "User submitted", UserSubmitted.class));
        alerts.addItem(new SectionItem(home, "Manage Alerts", ManageAlerts.class));

        Section maps = new Section(home, "Campus map", "maps", false);
        maps.addItem(new SectionItem(home, "Danger Zones"));
        maps.addItem(new SectionItem(home, "Evacuation Routes"));
        maps.addItem(new SectionItem(home, "Update map"));

        Section iot = new Section(home, "IoT", "sensor", false);
        iot.addItem(new SectionItem(home, "Manage Sensors", ManageSensors.class));

        addSection(alerts);
        separate(home.getWindow());
        addSection(maps);
        separate(home.getWindow());
        addSection(iot);

        applyStyle(home.getWindow().getStyl());
    }

    public void addSection(Section section) {
        getChildren().add(section);
        sections.add(section);
    }

    public boolean fire(String match) {
        for (Section section : sections) {
            if (section.fire(match)) {
                return true;
            }
        }

        return false;
    }

    public void separate(Window win) {
        Separator sep = new Separator(win, Orientation.HORIZONTAL);
        getChildren().addAll(new FixedVSpace(10), sep, new FixedVSpace(8));
    }

    @Override
    public void applyStyle(Style style) {
        //TODO
    }

    @Override
    public void applyStyle(ObjectProperty<Style> style) {
        Styleable.bindStyle(this, style);
    }
}
