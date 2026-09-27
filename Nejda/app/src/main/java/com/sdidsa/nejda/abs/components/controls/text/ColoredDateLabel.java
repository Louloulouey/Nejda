package com.sdidsa.nejda.abs.components.controls.text;

import android.content.Context;

import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.style.Styleable;
import com.sdidsa.nejda.abs.style.StyleToColor;
import com.sdidsa.nejda.abs.utils.view.StyleUtils;

public class ColoredDateLabel extends DateLabel implements Styleable {
    private StyleToColor fill;
    private final StyleToColor back;

    public ColoredDateLabel(Context context) {
        this(context, Style.TEXT_NORM);
    }

    public ColoredDateLabel(Context owner, StyleToColor fill) {
        this(owner, fill, null);
    }

    public ColoredDateLabel(Context owner, StyleToColor fill, StyleToColor back) {
        super(owner);

        this.fill = fill;
        this.back = back;

        applyStyle(StyleUtils.getStyle(owner));
    }

    public void setFill(StyleToColor fill) {
        this.fill = fill;
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
