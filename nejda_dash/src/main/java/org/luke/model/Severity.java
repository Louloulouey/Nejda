package org.luke.model;

import javafx.scene.paint.Color;
import org.luke.gui.controls.input.combo.ComboInput;
import org.luke.gui.controls.input.combo.ComboItem;
import org.luke.gui.controls.popup.context.items.MenuItem;
import org.luke.gui.style.Style;

import java.util.function.Function;

public enum Severity {
    MINOR("Minor", 0.5f, 1),
    MAJOR("Major", 0.75f, 2),
    CRITICAL("Critical", 1.0f, 3);

    private final String text;
    private final float severity;
    private final Function<Style, Color> color;
    private final int severityValue;

    Severity(String text, float severity, int severityValue) {
        this.severityValue = severityValue;
        this.text = text;
        this.severity = severity;
        this.color = style ->
                Style.interpolateColor(style.getTextPositive(), style.getTextDanger(), severity);
    }

    public static Severity bySeverity(int severity) {
        for (Severity value : values()) {
            if (value.severityValue == severity) {
                return value;
            }
        }
        return null;
    }

    private ComboItem item;
    public ComboItem toCombo(ComboInput input) {
        if(item == null) item = new ComboItem() {
            MenuItem item;

            @Override
            public String getDisplay() {
                return text;
            }

            @Override
            public String getValue() {
                return severityValue + "";
            }

            @Override
            public MenuItem menuItem() {
                if(item == null) item = new MenuItem(input.getPopup(), text);
                return item;
            }

            @Override
            public boolean match(String other) {
                return text.equals(other) || (severityValue + "").equals(other);
            }
        };
        return item;
    }

    public String getText() {
        return text;
    }

    public float getSeverity() {
        return severity;
    }

    public int getSeverityValue() {
        return severityValue;
    }

    public Function<Style, Color> getColor() {
        return color;
    }
}
