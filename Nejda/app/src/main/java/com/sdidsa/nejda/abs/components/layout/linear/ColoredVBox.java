package com.sdidsa.nejda.abs.components.layout.linear;

import android.content.Context;

import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.style.Styleable;
import com.sdidsa.nejda.abs.style.StyleToColor;
import com.sdidsa.nejda.abs.utils.view.StyleUtils;

public class ColoredVBox extends VBox implements Styleable {
    private StyleToColor fill;

    public ColoredVBox(Context owner) {
        this(owner, Style.BACK_PRI);
    }

    public ColoredVBox(Context owner, StyleToColor fill) {
        super(owner);
        this.fill = fill;

        applyStyle(StyleUtils.getStyle(owner));
    }

    public void setFill(StyleToColor fill) {
        this.fill = fill;
        applyStyle(StyleUtils.getStyle(owner).get());
    }

    @Override
    public void applyStyle(Style style) {
        setBackground(fill.get(style));
    }

}
