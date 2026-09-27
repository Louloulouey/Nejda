package org.luke.nejda.app.home.right.emergency.requests;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.luke.controll.AlertApi;
import org.luke.gui.controls.Font;
import org.luke.gui.controls.image.ColoredIcon;
import org.luke.gui.controls.label.keyed.Label;
import org.luke.gui.controls.space.ExpandingHSpace;
import org.luke.gui.controls.space.Separator;
import org.luke.gui.style.Style;
import org.luke.model.AlertRequest;
import org.luke.nejda.app.home.Home;
import org.luke.nejda.app.home.right.HomeContent;
import org.luke.nejda.app.home.right.emergency.requests.open.OpenAlertRequest;

import java.util.HashSet;

public class UserSubmitted extends HomeContent {
    private final VBox list;
    private final VBox open;
    private final Label filtersLab;
    private final HBox filters;

    private HashSet<String> enabled = new HashSet<>();

    public UserSubmitted(Home home) {
        super(home);

        addHeader(home.getWindow(), "User submitted reports",
                new ColoredIcon(home.getWindow(), "refresh", 24, 18, Style::getTextNormal)
                        .setOnAction(this::fetch)
        );

        HBox content = new HBox();
        VBox.setVgrow(content, Priority.ALWAYS);

        list = new VBox();
        list.setSpacing(10);
        list.setPadding(new Insets(10));

        open = new VBox();
        open.setPadding(new Insets(10));

        list.setMinWidth(USE_PREF_SIZE);
        list.setMaxWidth(USE_PREF_SIZE);
        open.setMinWidth(USE_PREF_SIZE);
        open.setMaxWidth(USE_PREF_SIZE);

        list.prefWidthProperty().bind(widthProperty().divide(2).subtract(1));
        open.prefWidthProperty().bind(widthProperty().divide(2).subtract(1));

        Separator separator = new Separator(home.getWindow(), Orientation.VERTICAL);

        content.getChildren().addAll(list, separator, open);

        filters = new HBox();
        filters.setAlignment(Pos.CENTER_RIGHT);
        filters.setSpacing(5);

        RequestFilter fire = new RequestFilter(home.getWindow(),
                "fire", "Filter by type : Fire");
        fire.enabledProperty().addListener((obs, ov, nv) -> {
            if (nv) {
                enabled.add("fire");
            } else {
                enabled.remove("fire");
            }

            fetch();
        });
        RequestFilter earthquake = new RequestFilter(home.getWindow(),
                "earthquake", "Filter by type : Earthquake");
        earthquake.enabledProperty().addListener((obs, ov, nv) -> {
            if (nv) {
                enabled.add("earthquake");
            } else {
                enabled.remove("earthquake");
            }

            fetch();
        });

        RequestFilter other = new RequestFilter(home.getWindow(),
                "other", "Filter by type : Other");
        other.enabledProperty().addListener((obs, ov, nv) -> {
            if (nv) {
                enabled.add("other");
            } else {
                enabled.remove("other");
            }

            fetch();
        });

        other.setEnabled(true);
        earthquake.setEnabled(true);
        fire.setEnabled(true);

        filtersLab = new Label(home.getWindow(), "Show requests of type : ");
        filtersLab.setFont(new Font(14));
        filters.setOpacity(0.7);

        filters.getChildren().addAll(filtersLab, new ExpandingHSpace(), fire, earthquake, other);

        getChildren().add(content);

        applyStyle(home.getWindow().getStyl());
    }

    public void open(AlertRequest req) {
        open.getChildren().setAll(new OpenAlertRequest(getHome().getWindow(), req, () -> {
            open.getChildren().clear();
            fetch();
        }));
    }

    public void fetch() {
        AlertApi.getRequests(requests -> {
            requests = requests.reversed();
            list.getChildren().setAll(filters);
            requests.stream().filter(r -> enabled.contains(r.getType().toLowerCase()))
                    .forEachOrdered(req -> {
                        list.getChildren().add(new AlertRequestItem(getHome().getWindow(), req, () -> open(req)));
                    });
        });
    }

    @Override
    public void setup() {
        fetch();
    }

    @Override
    public void applyStyle(Style style) {
        if (filtersLab == null) return;
        filtersLab.setFill(style.getTextNormal());
        super.applyStyle(style);
    }
}
