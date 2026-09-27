package com.sdidsa.nejda.abs.components.layout.overlay;

import androidx.annotation.DrawableRes;

import com.sdidsa.nejda.abs.utils.UiScale;

public record OverlayOption(String text, @DrawableRes int icon, boolean colored) {
    public OverlayOption(String text, int icon, boolean colored) {
        this.text = text;
        this.icon = icon;
        this.colored = colored;
    }

    public OverlayOption(String text, int icon) {
        this(text, icon, true);
    }

    public OverlayOption(String text) {
        this(text, -1, false);
    }

    public OverlayOption(UiScale scale, @DrawableRes int icon) {
        this(scale.getText(), icon);
    }

    public OverlayOption(UiScale scale) {
        this(scale.getText());
    }

}
