package com.sdidsa.nejda.abs.animation.view;

import android.view.View;

import com.sdidsa.nejda.abs.animation.base.ViewAnimation;

public class RotateAnimation extends ViewAnimation {

    public RotateAnimation(long duration, View view, float from, float to) {
        super(duration, view, from, to);
    }

    public RotateAnimation(View view, float from, float to) {
        super(view, from, to);
    }

    public RotateAnimation(View view, float to) {
        super(view, to);
    }

    @Override
    protected void apply(View view, float v) {
        view.setRotation(v);
    }

    @Override
    protected float getFrom(View view) {
        return view.getRotation();
    }
}
