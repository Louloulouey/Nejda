package org.luke.nejda.app.home;

import javafx.beans.property.ObjectProperty;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.window.Window;
import org.luke.nejda.app.home.right.HomeContent;

public class Right extends StackPane implements Styleable {
    public Right(Window window) {

        HBox.setHgrow(this, Priority.ALWAYS);

        applyStyle(window.getStyl());
    }

    public void load(HomeContent content) {
        getChildren().setAll(content);
    }

    @Override
    public void applyStyle(Style style) {
        setBackground(Backgrounds.make(style.getBackgroundTertiary(), 10));
    }

    @Override
    public void applyStyle(ObjectProperty<Style> style) {
        Styleable.bindStyle(this, style);
    }
}
