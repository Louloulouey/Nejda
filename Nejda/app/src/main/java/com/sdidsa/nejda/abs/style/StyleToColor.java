package com.sdidsa.nejda.abs.style;

import androidx.annotation.ColorInt;

import com.sdidsa.nejda.abs.App;

public interface StyleToColor {
    @ColorInt int get(Style style);

    static StyleToColor adjustAlpha(StyleToColor color, float factor) {
        return style -> App.adjustAlpha(color.get(style), factor);
    }
}
