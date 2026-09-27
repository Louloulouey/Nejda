package com.sdidsa.nejda.abs.animation.view;

import android.view.View;

import com.sdidsa.nejda.abs.animation.base.ViewAnimation;

public class ElevationAnimation extends ViewAnimation {

    public ElevationAnimation(View view, float to) {
        super(view, to);
    }

    @Override
    protected void apply(View view, float v) {
        view.setElevation(v);
    }

    @Override
    protected float getFrom(View view) {
        return view.getElevation();
    }
}
