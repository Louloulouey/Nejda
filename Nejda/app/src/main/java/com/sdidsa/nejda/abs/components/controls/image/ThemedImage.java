package com.sdidsa.nejda.abs.components.controls.image;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.DrawableRes;

import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.style.Styleable;
import com.sdidsa.nejda.abs.utils.view.StyleUtils;

public class ThemedImage extends Image implements Styleable {
    private @DrawableRes int darkRes;
    private @DrawableRes int lightRes;

    public ThemedImage(Context owner) {
        this(owner, -1, -1);
    }

    public ThemedImage(Context owner, int darkRes, int lightRes) {
        super(owner);

        this.darkRes = darkRes;
        this.lightRes = lightRes;

        applyStyle(StyleUtils.getStyle(owner));
    }

    @Override
    public void setImageResource(int resId) {
        this.darkRes = -1;
        this.lightRes = -1;
        super.setImageResource(resId);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        this.darkRes = -1;
        this.lightRes = -1;
        super.setImageBitmap(bm);
    }

    @Override
    public void applyStyle(Style style) {
        if(darkRes == -1 || lightRes == -1) return;
        if(style.isDark()) {
            super.setImageResource(darkRes);
        }else {
            super.setImageResource(lightRes);
        }
    }

}
