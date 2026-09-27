package org.luke.nejda.app.home.right.emergency.alerts;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontWeight;
import org.luke.controll.AlertApi;
import org.luke.gui.controls.Font;
import org.luke.gui.controls.image.ColoredIcon;
import org.luke.gui.controls.label.DateFormat;
import org.luke.gui.controls.label.DateLabel;
import org.luke.gui.controls.label.keyed.Label;
import org.luke.gui.controls.popup.Direction;
import org.luke.gui.controls.popup.tooltip.TextTooltip;
import org.luke.gui.controls.space.ExpandingHSpace;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.window.Window;
import org.luke.model.Alert;
import org.luke.model.Severity;

public class AlertItem extends HBox implements Styleable {
    private final Label titleLab;
    private final Label bodyLab;
    private final Label title;
    private final Label body;
    private final DateLabel date;
    private final Alert active;

    public AlertItem(Window window, Alert request, Runnable refresh) {
        active = request;
        setPadding(new Insets(15));
        setSpacing(15);

        setAlignment(Pos.CENTER);

        VBox texts = new VBox();
        texts.setSpacing(5);
        texts.setAlignment(Pos.CENTER_LEFT);
        title = new Label(window, request.getTitle() + (request.getActive() ? " - Active" : ""));
        title.setFont(new Font(16, FontWeight.MEDIUM));
        body = new Label(window, request.getBody());

        texts.getChildren().addAll(title, body);

        VBox labs = new VBox();
        labs.setSpacing(5);
        labs.setAlignment(Pos.CENTER_LEFT);

        titleLab = new Label(window, "Title");
        titleLab.setFont(new Font(16, FontWeight.MEDIUM));
        bodyLab = new Label(window, "description");

        labs.getChildren().addAll(titleLab, bodyLab);

        setCursor(Cursor.HAND);

        date = new DateLabel(window);
        date.setFormat(DateFormat.FULL_LONG);
        date.setDate(request.getCreatedAt());

        ColoredIcon delete = new ColoredIcon(window, "erase", 20, Style::getTextNormal);
        delete.setPadding(10);
        TextTooltip.install(delete, Direction.UP, "Delete Alert");

        delete.setAction(() -> {
            AlertApi.delete(request, resp -> {
                if(refresh != null) refresh.run();
            });
        });

        getChildren().addAll(labs, texts, new ExpandingHSpace(), date, delete);

        if(request.getActive()) {
            ColoredIcon disable = new ColoredIcon(window, "end", 20, Style::getTextNormal);
            disable.setPadding(10);
            TextTooltip.install(disable, Direction.UP, "Stop Alert");
            getChildren().add(disable);

            disable.setAction(() -> {
                AlertApi.end(request, resp -> {
                    if(refresh != null) refresh.run();
                });
            });
        }


        applyStyle(window.getStyl());
    }

    @Override
    public void applyStyle(Style style) {
        title.setFill(active.getActive() ?
                Severity.bySeverity(active.getSeverity()).getColor().apply(style) :
                style.getTextNormal());
        body.setFill(active.getActive() ?
                Severity.bySeverity(active.getSeverity()).getColor().apply(style) :
                style.getTextNormal());
        date.setFill(style.getTextNormal());

        titleLab.setFill(style.getTextMuted());
        bodyLab.setFill(style.getTextMuted());

        backgroundProperty().bind(Bindings.when(hoverProperty()).then(
                Backgrounds.make(style.getBackgroundPrimary(), 10)
        ).otherwise(
                Backgrounds.make(style.getBackgroundSecondary(), 10)
        ));
    }

    @Override
    public void applyStyle(ObjectProperty<Style> style) {
        Styleable.bindStyle(this, style);
    }
}
