package com.sdidsa.nejda.abs.components.layout;

import android.content.Context;

import androidx.annotation.ColorInt;

import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.style.Styleable;
import com.sdidsa.nejda.abs.style.StyleToColor;
import com.sdidsa.nejda.abs.utils.view.StyleUtils;

public class ColoredStackPane extends StackPane implements Styleable {
    private StyleToColor fill;
    private final StyleToColor stroke;
    private final int strokeWidth;

    public ColoredStackPane(Context owner) {
        this(owner, null, null, -1);
    }

    public ColoredStackPane(Context owner, StyleToColor fill, StyleToColor stroke, int strokeWidth) {
        super(owner);
        this.fill = fill;
        this.stroke = stroke;
        this.strokeWidth = strokeWidth;

        applyStyle(StyleUtils.getStyle(owner));
    }

    public void setFill(StyleToColor fill) {
        this.fill = fill;
        applyStyle(StyleUtils.getStyle(owner).get());
    }

    public void setFill(@ColorInt int color, StyleToColor fill) {
        this.fill = fill;
        setBackground(color);
    }

    public ColoredStackPane(Context owner, StyleToColor fill) {
        this(owner, fill, null, -1);
    }

    @Override
    public void applyStyle(Style style) {
        if(fill != null) {
            setBackground(fill.get(style));
        }
        if(stroke != null) {
            setBorder(stroke.get(style), strokeWidth);
        }
    }

}
