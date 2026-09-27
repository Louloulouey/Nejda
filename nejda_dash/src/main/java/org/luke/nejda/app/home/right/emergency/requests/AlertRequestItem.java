package org.luke.nejda.app.home.right.emergency.requests;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontWeight;
import org.luke.gui.controls.Font;
import org.luke.gui.controls.image.ColorIcon;
import org.luke.gui.controls.image.ColoredIcon;
import org.luke.gui.controls.label.keyed.Label;
import org.luke.gui.controls.space.ExpandingHSpace;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.window.Window;
import org.luke.model.AlertRequest;

public class AlertRequestItem extends HBox implements Styleable {
    private final Label title;
    private final Label body;

    private final ColorIcon icon;

    public AlertRequestItem(Window window, AlertRequest request, Runnable onClick) {
        setPadding(new Insets(15));

        setAlignment(Pos.CENTER);

        icon = new ColoredIcon(window, request.getType().toLowerCase(), 48, Style::getTextMuted);

        VBox texts = new VBox();
        texts.setSpacing(5);
        texts.setAlignment(Pos.CENTER_LEFT);
        texts.setPadding(new Insets(0,0,0,15));
        title = new Label(window, request.getType());
        title.setFont(new Font(17, FontWeight.BOLD));
        body = new Label(window, request.getDetails());

        texts.getChildren().addAll(title, body);

        setCursor(Cursor.HAND);
        setOnMouseClicked(e -> onClick.run());

        ColoredIcon open = new ColoredIcon(window, "arrow", 28, 24, Style::getTextNormal);

        getChildren().addAll(icon, texts, new ExpandingHSpace(), open);

        applyStyle(window.getStyl());
    }

    @Override
    public void applyStyle(Style style) {
        title.setFill(style.getTextNormal());
        body.setFill(style.getTextNormal());
        icon.setBackground(Backgrounds.make(style.getBackgroundTertiary(), 8));

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
