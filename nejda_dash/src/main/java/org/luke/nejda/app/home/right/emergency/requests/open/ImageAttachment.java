package org.luke.nejda.app.home.right.emergency.requests.open;

import javafx.beans.binding.Bindings;
import javafx.scene.Cursor;
import org.luke.gui.controls.image.ColorIcon;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.window.Window;
import org.luke.model.AlertRequestAttachment;

public class ImageAttachment extends ColorIcon implements Styleable {
    public ImageAttachment(Window window, AlertRequestAttachment attachment) {
        super(attachment.getUrl(), 64);
        super.setCrop(true);
        super.noColor();
        super.round(8);

        setCursor(Cursor.HAND);

        opacityProperty().bind(Bindings.when(hoverProperty()).then(0.7).otherwise(1));

        applyStyle(window.getStyl());
    }

    @Override
    public void applyStyle(Style style) {
        super.applyStyle(style);
        setBackground(Backgrounds.make(style.getBackgroundTertiary(), 8));
    }
}
