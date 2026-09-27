package com.sdidsa.nejda.abs.components.controls.shape;

import android.content.Context;

import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.style.StyleToColor;
import com.sdidsa.nejda.abs.style.Styleable;
import com.sdidsa.nejda.abs.utils.view.StyleUtils;

public class ColoredRectangle extends Rectangle implements Styleable {
    private final StyleToColor fill;

    public ColoredRectangle(Context owner) {
        this(owner, null);
    }

    public ColoredRectangle(Context owner, StyleToColor fill) {
        super(owner);
        this.fill = fill;

        applyStyle(StyleUtils.getStyle(owner));
    }

    @Override
    public void applyStyle(Style style) {
        if(fill != null)
            setFill(fill.get(style));
    }

}
