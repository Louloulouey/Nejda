package com.sdidsa.nejda.abs.animation.view.scale;

import android.view.View;

import com.sdidsa.nejda.abs.animation.base.ViewAnimation;

public class PivotXAnimation extends ViewAnimation {

    public PivotXAnimation(View view, float to) {
        super(view, to);
    }

    @Override
    protected float getFrom(View view) {
        return view.getPivotX();
    }

    @Override
    protected void apply(View view, float v) {
        view.setPivotX(v);
    }
}
