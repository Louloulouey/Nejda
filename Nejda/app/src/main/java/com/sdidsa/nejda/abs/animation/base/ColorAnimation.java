package com.sdidsa.nejda.abs.animation.base;

import androidx.annotation.ColorInt;

import com.sdidsa.nejda.abs.style.Style;

public abstract class ColorAnimation extends Animation {
    private final @ColorInt int fromColor;
    private final @ColorInt int toColor;

    public ColorAnimation(@ColorInt int from, @ColorInt int to) {
        super();
        fromColor = from;
        toColor = to;
    }

    @Override
    public void update(float v) {
        updateValue(Style.interpolateColor(fromColor, toColor, v));
    }

    public abstract void updateValue(@ColorInt int color);
}
