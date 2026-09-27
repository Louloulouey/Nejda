package org.luke.nejda.app.home.right.emergency.requests.open;

import org.luke.gui.controls.Font;
import org.luke.gui.controls.label.DateFormat;
import org.luke.gui.controls.label.DateLabel;
import org.luke.gui.style.Style;
import org.luke.gui.window.Window;

import java.time.LocalDateTime;

public class DateDetail extends RequestDetail {

    private final DateLabel body;

    public DateDetail(Window window, String titleStr, LocalDateTime bodyStr) {
        super(window, titleStr);
        body = new DateLabel(window);
        body.setFormat(DateFormat.FULL_LONG);
        body.setIgnoreTime(true);
        body.setDate(bodyStr);
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
