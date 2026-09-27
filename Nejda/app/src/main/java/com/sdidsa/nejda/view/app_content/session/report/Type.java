package com.sdidsa.nejda.view.app_content.session.report;

public enum Type {
    EARTHQUAKE("Earthquake"),
    FIRE("Fire"),
    OTHER("Other");
    private final String text;

    Type(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static Type byText(String text) {
        for (Type value : values()) {
            if (value.text.equalsIgnoreCase(text)) {
                return value;
            }
        }
        return null;
    }
}
