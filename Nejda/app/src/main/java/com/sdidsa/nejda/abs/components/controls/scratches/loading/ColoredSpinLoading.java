package com.sdidsa.nejda.abs.components.controls.scratches.loading;

import android.content.Context;

import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.style.Styleable;
import com.sdidsa.nejda.abs.style.StyleToColor;
import com.sdidsa.nejda.abs.utils.view.StyleUtils;

public class ColoredSpinLoading extends SpinLoading implements Styleable {
    private StyleToColor fill;

    public ColoredSpinLoading(Context context) {
        this(context, Style.TEXT_SEC);
    }

    public ColoredSpinLoading(Context owner, StyleToColor fill) {
        super(owner);
        this.fill = fill;
        applyStyle(StyleUtils.getStyle(owner));
    }

    public ColoredSpinLoading(Context owner, StyleToColor fill, float size) {
        this(owner, fill);
        setSize(size);
    }

    public void setFill(StyleToColor fill) {
        this.fill = fill;
        applyStyle(StyleUtils.getStyle(owner).get());
    }

    @Override
    public void applyStyle(Style style) {
        setFill(fill.get(style));
    }

}
