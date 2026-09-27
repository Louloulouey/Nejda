package com.sdidsa.nejda.abs.utils;

public enum UiScale {
    AUTO(1, "ui_scale_auto"),
    SMALLEST(0.75f, "ui_scale_smallest"),
    SMALLER(0.875f, "ui_scale_smaller"),
    NORMAL(1, "ui_scale_normal"),
    BIGGER(1.15f, "ui_scale_bigger"),
    BIGGEST(1.3f, "ui_scale_biggest");

    private final float scale;
    private final String text;

    UiScale(float scale, String text) {
        this.scale = scale;
        this.text = text;
    }

    public float getScale() {
        return scale;
    }

    public String getText() {
        return text;
    }

    public static UiScale forText(String text) {
        for(UiScale scale : values()) {
            if(scale.text.equals(text))
                return scale;
        }
        return NORMAL;
    }
}
