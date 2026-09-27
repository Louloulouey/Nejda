package org.luke.nejda.app.home.right.emergency.requests.open;

import org.luke.gui.controls.Font;
import org.luke.gui.controls.label.keyed.Label;
import org.luke.gui.style.Style;
import org.luke.gui.window.Window;

public class TextDetail extends RequestDetail {

    private final Label body;

    public TextDetail(Window window, String titleStr, String bodyStr) {
        super(window, titleStr);
        body = new Label(window, bodyStr);
        body.setFont(new Font(16));

        getChildren().add(body);

        applyStyle(window.getStyl());
    }

    @Override
    public void applyStyle(Style style) {
        if(body == null) return;

        body.setFill(style.getTextNormal());
        super.applyStyle(style);
    }
}
