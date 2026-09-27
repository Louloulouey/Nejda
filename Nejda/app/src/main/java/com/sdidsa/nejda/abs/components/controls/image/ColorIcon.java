package com.sdidsa.nejda.abs.components.controls.image;

import android.content.Context;
import android.graphics.PorterDuff;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;

public class ColorIcon extends Image {

    public ColorIcon(Context owner, @DrawableRes int id, float size) {
        super(owner);
        if (id != -1)
            setImageResource(id);

        setSize(size);
    }

    public ColorIcon(Context owner, @DrawableRes int id) {
        super(owner);
        if (id != -1)
            setImageResource(id);
    }

    public ColorIcon(Context owner) {
        this(owner, -1);
    }

    public void setColor(@ColorInt int color) {
        view.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    public void setFill(int fill) {
        setColor(fill);
    }
}
