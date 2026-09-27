package com.sdidsa.nejda.abs.animation.view;

import android.view.View;

import com.sdidsa.nejda.abs.animation.base.ViewAnimation;

public class WidthAnimation extends ViewAnimation {

    public WidthAnimation(View view, float to) {
        super(view, to);
    }

    public WidthAnimation(long duration, View view, float to) {
        super(duration, view, to);
    }

    public WidthAnimation(View view, float from, float to) {
        super(view, from, to);
    }

    public WidthAnimation(long duration, View view, float from, float to) {
        super(duration, view, from, to);
    }

    @Override
    protected float getFrom(View view) {
        return view.getLayoutParams().width;
    }

    @Override
    protected void apply(View view, float v) {
        view.getLayoutParams().width = Math.max((int) v, 0);
        view.setLayoutParams(view.getLayoutParams());
    }
}
