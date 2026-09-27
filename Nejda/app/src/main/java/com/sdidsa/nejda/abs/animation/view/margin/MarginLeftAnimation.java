package com.sdidsa.nejda.abs.animation.view.margin;

import android.view.View;
import android.view.ViewGroup;

import com.sdidsa.nejda.abs.animation.base.ViewAnimation;

public class MarginLeftAnimation extends ViewAnimation {

    private float fromLeft;
    private final int toLeft;

    public MarginLeftAnimation(long duration, View view, int toLeft) {
        super(duration, view, 0, 1);

        this.toLeft = toLeft;
        fromLeft = -1;
    }

    public MarginLeftAnimation(View view, int fromLeft, int toLeft) {
        super(view, 0, 1);

        this.toLeft = toLeft;
        this.fromLeft = fromLeft;
    }

    public MarginLeftAnimation(View view, int toLeft) {
        super(view, 0, 1);

        this.toLeft = toLeft;
        fromLeft = -1;
    }

    @Override
    public void init() {
        super.init();
        if(fromLeft == -1) fromLeft = getFrom(getView());
    }

    private ViewGroup.MarginLayoutParams getLayoutParams() {
        return (ViewGroup.MarginLayoutParams) getView().getLayoutParams();
    }

    @Override
    protected float getFrom(View view) {
        return getLayoutParams().getMarginStart();
    }

    @Override
    protected void apply(View view, float v) {
        getLayoutParams().setMarginStart((int) (fromLeft + (toLeft - fromLeft) * v));
        view.setLayoutParams(getLayoutParams());
    }
}
