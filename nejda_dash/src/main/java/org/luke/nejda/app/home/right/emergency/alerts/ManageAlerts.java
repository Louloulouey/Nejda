package org.luke.nejda.app.home.right.emergency.alerts;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import org.luke.controll.AlertApi;
import org.luke.gui.controls.image.ColoredIcon;
import org.luke.gui.controls.space.FixedHSpace;
import org.luke.gui.style.Style;
import org.luke.model.Alert;
import org.luke.nejda.app.home.Home;
import org.luke.nejda.app.home.right.HomeContent;
import org.luke.nejda.app.home.right.emergency.CreateAlert;
import org.luke.nejda.ui.Button;

public class ManageAlerts extends HomeContent {
    private final VBox alerts;
    public ManageAlerts(Home home) {
        super(home);

        Button add = new Button(home.getWindow(), "Create", 160);
        add.add(
                new ColoredIcon(home.getWindow(), "add", 24, 18, Style::getTextOnAccent),
                new FixedHSpace(10)
        );
        add.setAction(() -> {
            Alert alert = new CreateAlert(home.getWindow())
                    .showForResult();
            if(alert == null) return;
            add.startLoading();
            AlertApi.createAlert(alert, resp -> {
                add.stopLoading();
                fetch();
            });
        });

        alerts = new VBox();
        alerts.setPadding(new Insets(10));
        alerts.setSpacing(10);

        fetch();

        addHeader(home.getWindow(), "Manage Alerts", new ColoredIcon(home.getWindow(),
                "refresh", 24, 20, Style::getTextNormal)
                .setOnAction(this::fetch), add);

        getChildren().add(alerts);

        applyStyle(home.getWindow().getStyl());
    }

    public void fetch() {
        AlertApi.getAll(alerts -> {
            this.alerts.getChildren().clear();
            alerts = alerts.reversed();
            alerts.forEach(alert -> {
                this.alerts.getChildren().add(new AlertItem(getHome().getWindow(), alert, this::fetch));
            });
        });
    }

    @Override
    public void setup() {
        fetch();
    }
}
