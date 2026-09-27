package com.sdidsa.nejda.abs.components.controls.image;

import android.content.Context;

import androidx.annotation.DrawableRes;

import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.style.Styleable;
import com.sdidsa.nejda.abs.style.StyleToColor;
import com.sdidsa.nejda.abs.utils.view.StyleUtils;

public class ColoredIcon extends ColorIcon implements Styleable {
    private StyleToColor fill;
    private StyleToColor back;
    private StyleToColor border;
    private float borderWidth;

    public ColoredIcon(Context owner) {
        this(owner, Style.ACCENT, -1);
    }

    public ColoredIcon(Context owner, StyleToColor color , int id) {
        this(owner, color, null, id);
    }

    public ColoredIcon(Context owner, StyleToColor fill, StyleToColor back , int id) {
        super(owner, id);
        this.fill = fill;
        setBack(back);

        applyStyle(StyleUtils.getStyle(owner));
    }

    public ColoredIcon(Context owner, StyleToColor fill, StyleToColor back , @DrawableRes int id, float size) {
        this(owner, fill, back, id);
        setSize(size);
    }

    public ColoredIcon(Context owner, StyleToColor fill ,int id, float size) {
        this(owner, fill, null, id, size);
    }

    public void setFill(StyleToColor fill) {
        this.fill = fill;
        applyStyle(StyleUtils.getStyle(owner).get());
    }

    public void setBack(StyleToColor back) {
        this.back = back;
        applyStyle(StyleUtils.getStyle(owner).get());
    }

    public void setBorder(StyleToColor border, float width) {
        this.border = border;
        this.borderWidth = width;
        applyStyle(StyleUtils.getStyle(owner).get());
    }

    @Override
    public void applyStyle(Style style) {
        if(fill != null) {
            setFill(fill.get(style));
        }
        if(back != null) {
            setBackgroundColor(back.get(style));
        }
        if(border != null) {
            setBorder(border.get(style), borderWidth);
        }
    }

}
