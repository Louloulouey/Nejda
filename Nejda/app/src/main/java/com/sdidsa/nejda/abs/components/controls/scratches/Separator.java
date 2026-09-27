package com.sdidsa.nejda.abs.components.controls.scratches;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;

import com.sdidsa.nejda.abs.utils.view.SizeUtils;

public class Separator extends View {
    private final Context owner;

    private Orientation orientation;
    private final float margin;

    private float thickness = 1;

    public Separator(Context owner) {
        this(owner, Orientation.HORIZONTAL, 0);
    }

    public Separator(Context owner, Orientation orientation, float margin) {
        super(owner);
        this.owner = owner;

        this.orientation = orientation;
            this.margin = margin;
        apply();
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
        apply();
    }

    public void setThickness(float thickness) {
        this.thickness = thickness;
        apply();
    }

    private void apply() {
        boolean isVert = orientation == Orientation.VERTICAL;
        boolean hor = !isVert;

        int thickInt = SizeUtils.dipToPx(thickness, owner);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                hor ? -1 : thickInt,
                hor ? thickInt : -1);


        int marginPx = SizeUtils.dipToPx(margin, owner);
        params.leftMargin = isVert ? 0 : marginPx;
        params.rightMargin = isVert ? 0 : marginPx;
        params.topMargin = isVert ? marginPx : 0;
        params.bottomMargin = isVert ? marginPx : 0;

        setLayoutParams(params);
    }

    public void setColor(@ColorInt int color) {
        setBackgroundColor(color);
    }


}
