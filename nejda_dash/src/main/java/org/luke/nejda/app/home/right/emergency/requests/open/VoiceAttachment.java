package org.luke.nejda.app.home.right.emergency.requests.open;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.layout.StackPane;
import org.luke.gui.controls.image.ColoredIcon;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.window.Window;
import org.luke.model.AlertRequestAttachment;

public class VoiceAttachment extends StackPane implements Styleable {
    public VoiceAttachment(Window window, AlertRequestAttachment attachment) {
        setMinSize(64,64);
        setMaxSize(64, 64);

        getChildren().add(
                new ColoredIcon(window, "play", 16, Style::getTextNormal)
        );

        setCursor(Cursor.HAND);

        opacityProperty().bind(Bindings.when(hoverProperty()).then(0.7).otherwise(1));

        applyStyle(window.getStyl());
    }

    @Override
    public void applyStyle(Style style) {
        setBackground(Backgrounds.make(style.getBackgroundTertiary(), 8));
    }

    @Override
    public void applyStyle(ObjectProperty<Style> style) {
        Styleable.bindStyle(this, style);
    }
}
