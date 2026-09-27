package com.sdidsa.nejda.abs.components.controls.text;

import android.content.Context;

import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.style.Styleable;
import com.sdidsa.nejda.abs.style.StyleToColor;
import com.sdidsa.nejda.abs.utils.view.StyleUtils;

public class ColoredLabel extends Label implements Styleable {
    private StyleToColor fill;
    private StyleToColor back;

    public ColoredLabel(Context context) {
        this(context, Style.TEXT_NORM, "Label text");
    }

    public ColoredLabel(Context owner, StyleToColor fill, String key) {
        this(owner, fill, null, key);
    }

    public ColoredLabel(Context owner, StyleToColor fill, StyleToColor back, String key) {
        super(owner, key);

        this.fill = fill;
        this.back = back;

        applyStyle(StyleUtils.getStyle(owner));
    }

    public void setFill(StyleToColor fill) {
        this.fill = fill;
        applyStyle(StyleUtils.getStyle(owner).get());
    }

    public void setBack(StyleToColor back) {
        this.back = back;
        applyStyle(StyleUtils.getStyle(owner).get());
    }

    @Override
    public void applyStyle(Style style) {
        if(fill != null) {
            setFill(fill.get(style));
        }
        if(back != null) {
            setBackground(back.get(style));
        }
    }

}
