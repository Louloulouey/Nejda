package org.luke.nejda.app.home.right.emergency.requests.open;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontWeight;
import org.luke.gui.controls.Font;
import org.luke.gui.controls.label.keyed.Label;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.window.Window;

public class RequestDetail extends VBox implements Styleable {

    private final Label title;

    public RequestDetail(Window window, String titleStr) {
        setPadding(new Insets(8));
        setSpacing(3);
        HBox.setHgrow(this, Priority.ALWAYS);

        title = new Label(window, titleStr);
        title.opacityProperty().unbind();
        title.setFont(new Font(12, FontWeight.BOLD));
        title.setOpacity(0.6);

        getChildren().add(title);

        applyStyle(window.getStyl());
    }

    @Override
    public void applyStyle(Style style) {
        setBackground(Backgrounds.make(style.getBackgroundTertiary(), 8));
        title.setFill(style.getTextNormal());
    }

    @Override
    public void applyStyle(ObjectProperty<Style> style) {
        Styleable.bindStyle(this, style);
    }
}
