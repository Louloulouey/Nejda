package com.sdidsa.nejda.view.app_content.session.report;

import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.style.StyleToColor;

public enum Severity {
    MINOR("Minor", 0.5f, 1),
    MAJOR("Major", 0.75f, 2),
    CRITICAL("Critical", 1.0f, 3);

    private final String text;
    private final float severity;
    private final StyleToColor color;
    private final int severityValue;

    Severity(String text, float severity, int severityValue) {
        this.severityValue = severityValue;
        this.text = text;
        this.severity = severity;
        this.color = style ->
                Style.interpolateColor(style.getTextPositive(), style.getTextError(), severity);
    }

    public static Severity bySeverity(int severity) {
        for (Severity value : values()) {
            if (value.severityValue == severity) {
                return value;
            }
        }
        return null;
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

    public StyleToColor getColor() {
        return color;
    }
}
